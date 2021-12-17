package com.refinedmods.refinedpipes.network;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.PipeFactory;
import com.refinedmods.refinedpipes.network.pipe.PipeRegistry;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class NetworkManager extends SavedData {
    private static final String NAME = RefinedPipes.ID + "_networks";
    private static final Logger LOGGER = LogManager.getLogger(NetworkManager.class);
    private final Level level;
    private final Map<String, Network> networks = new HashMap<>();
    private final Map<BlockPos, Pipe> pipes = new HashMap<>();

    public NetworkManager(Level level) {
        this.level = level;
    }

    public static NetworkManager get(Level level) {
        return get((ServerLevel) level);
    }

    public static NetworkManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent((tag) -> {
            NetworkManager networkManager = new NetworkManager(level);
            networkManager.load(tag);
            return networkManager;
        }, () -> new NetworkManager(level), NAME);
    }

    public void addNetwork(Network network) {
        if (networks.containsKey(network.getId())) {
            throw new RuntimeException("Duplicate network " + network.getId());
        }

        networks.put(network.getId(), network);

        LOGGER.debug("Network {} created", network.getId());

        setDirty();
    }

    public void removeNetwork(String id) {
        if (!networks.containsKey(id)) {
            throw new RuntimeException("Network " + id + " not found");
        }

        networks.remove(id);

        LOGGER.debug("Network {} removed", id);

        setDirty();
    }

    private void formNetworkAt(Level level, BlockPos pos, ResourceLocation type) {
        Network network = NetworkRegistry.INSTANCE.getFactory(type).create(pos);

        addNetwork(network);

        network.scanGraph(level, pos);
    }

    private void mergeNetworksIntoOne(Set<Pipe> candidates, Level level, BlockPos pos) {
        if (candidates.isEmpty()) {
            throw new RuntimeException("Cannot merge networks: no candidates");
        }

        Set<Network> networkCandidates = new HashSet<>();

        for (Pipe candidate : candidates) {
            if (candidate.getNetwork() == null) {
                throw new RuntimeException("Pipe network is null!");
            }

            networkCandidates.add(candidate.getNetwork());
        }

        Iterator<Network> networks = networkCandidates.iterator();

        Network mainNetwork = networks.next();

        Set<Network> mergedNetworks = new HashSet<>();

        while (networks.hasNext()) {
            // Remove all the other networks.
            Network otherNetwork = networks.next();

            boolean canMerge = mainNetwork.getType().equals(otherNetwork.getType());

            if (canMerge) {
                mergedNetworks.add(otherNetwork);

                removeNetwork(otherNetwork.getId());
            }
        }

        mainNetwork.scanGraph(level, pos);

        mergedNetworks.forEach(n -> n.onMergedWith(mainNetwork));
    }

    public void addPipe(Pipe pipe) {
        if (pipes.containsKey(pipe.getPos())) {
            throw new RuntimeException("Pipe at " + pipe.getPos() + " already exists");
        }

        pipes.put(pipe.getPos(), pipe);

        LOGGER.debug("Pipe added at {}", pipe.getPos());

        setDirty();

        Set<Pipe> adjacentPipes = findAdjacentPipes(pipe.getPos(), pipe.getNetworkType());

        if (adjacentPipes.isEmpty()) {
            formNetworkAt(pipe.getLevel(), pipe.getPos(), pipe.getNetworkType());
        } else {
            mergeNetworksIntoOne(adjacentPipes, pipe.getLevel(), pipe.getPos());
        }
    }

    public void removePipe(BlockPos pos) {
        Pipe pipe = getPipe(pos);
        if (pipe == null) {
            throw new RuntimeException("Pipe at " + pos + " was not found");
        }

        if (pipe.getNetwork() == null) {
            LOGGER.warn("Removed pipe at {} has no associated network", pipe.getPos());
        }

        pipes.remove(pipe.getPos());

        LOGGER.debug("Pipe removed at {}", pipe.getPos());

        setDirty();

        if (pipe.getNetwork() != null) {
            splitNetworks(pipe);
        }
    }

    private void splitNetworks(Pipe originPipe) {
        // Sanity checks
        for (Pipe adjacent : findAdjacentPipes(originPipe.getPos(), originPipe.getNetworkType())) {
            if (adjacent.getNetwork() == null) {
                throw new RuntimeException("Adjacent pipe has no network");
            }

            if (adjacent.getNetwork() != originPipe.getNetwork()) {
                throw new RuntimeException("The origin pipe network is different than the adjacent pipe network");
            }
        }

        // We can assume all adjacent pipes (with the same network type) share the same network with the removed pipe.
        // That means it doesn't matter which pipe network we use for splitting, we'll take the first found one.
        Pipe otherPipeInNetwork = findFirstAdjacentPipe(originPipe.getPos(), originPipe.getNetworkType());

        if (otherPipeInNetwork != null) {
            otherPipeInNetwork.getNetwork().setOriginPos(otherPipeInNetwork.getPos());
            setDirty();

            NetworkGraphScannerResult result = otherPipeInNetwork.getNetwork().scanGraph(
                otherPipeInNetwork.getLevel(),
                otherPipeInNetwork.getPos()
            );

            // For sanity checking
            boolean foundRemovedPipe = false;

            for (Pipe removed : result.getRemovedPipes()) {
                // It's obvious that our removed pipe is removed.
                // We don't want to create a new splitted network for this one.
                if (removed.getPos().equals(originPipe.getPos())) {
                    foundRemovedPipe = true;
                    continue;
                }

                // The formNetworkAt call below can let these removed pipes join a network again.
                // We only have to form a new network when necessary, hence the null check.
                if (removed.getNetwork() == null) {
                    formNetworkAt(removed.getLevel(), removed.getPos(), removed.getNetworkType());
                }
            }

            if (!foundRemovedPipe) {
                throw new RuntimeException("Didn't find removed pipe when splitting network");
            }
        } else {
            LOGGER.debug("Removing empty network {}", originPipe.getNetwork().getId());

            removeNetwork(originPipe.getNetwork().getId());
        }
    }

    private Set<Pipe> findAdjacentPipes(BlockPos pos, ResourceLocation networkType) {
        Set<Pipe> pipes = new HashSet<>();

        for (Direction dir : Direction.values()) {
            Pipe pipe = getPipe(pos.relative(dir));

            if (pipe != null && pipe.getNetworkType().equals(networkType)) {
                pipes.add(pipe);
            }
        }

        return pipes;
    }

    @Nullable
    private Pipe findFirstAdjacentPipe(BlockPos pos, ResourceLocation networkType) {
        for (Direction dir : Direction.values()) {
            Pipe pipe = getPipe(pos.relative(dir));

            if (pipe != null && pipe.getNetworkType().equals(networkType)) {
                return pipe;
            }
        }

        return null;
    }

    @Nullable
    public Pipe getPipe(BlockPos pos) {
        return pipes.get(pos);
    }

    public Collection<Network> getNetworks() {
        return networks.values();
    }

    public void load(CompoundTag tag) {
        ListTag pipes = tag.getList("pipes", Tag.TAG_COMPOUND);
        for (Tag pipeTag : pipes) {
            CompoundTag pipeTagCompound = (CompoundTag) pipeTag;

            // @BC
            ResourceLocation factoryId = pipeTagCompound.contains("id") ? new ResourceLocation(pipeTagCompound.getString("id")) : ItemPipe.ID;

            PipeFactory factory = PipeRegistry.INSTANCE.getFactory(factoryId);
            if (factory == null) {
                LOGGER.warn("Pipe {} no longer exists", factoryId.toString());
                continue;
            }

            Pipe pipe = factory.createFromNbt(level, pipeTagCompound);

            this.pipes.put(pipe.getPos(), pipe);
        }

        ListTag nets = tag.getList("networks", Tag.TAG_COMPOUND);
        for (Tag netTag : nets) {
            CompoundTag netTagCompound = (CompoundTag) netTag;
            if (!netTagCompound.contains("type")) {
                LOGGER.warn("Skipping network without type");
                continue;
            }

            ResourceLocation type = new ResourceLocation(netTagCompound.getString("type"));

            NetworkFactory factory = NetworkRegistry.INSTANCE.getFactory(type);
            if (factory == null) {
                LOGGER.warn("Unknown network type {}", type.toString());
                continue;
            }

            Network network = factory.create(netTagCompound);

            networks.put(network.getId(), network);
        }

        LOGGER.debug("Read {} pipes", pipes.size());
        LOGGER.debug("Read {} networks", networks.size());
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag pipes = new ListTag();
        this.pipes.values().forEach(p -> {
            CompoundTag pipeTag = new CompoundTag();
            pipeTag.putString("id", p.getId().toString());
            pipes.add(p.writeToNbt(pipeTag));
        });
        tag.put("pipes", pipes);

        ListTag networks = new ListTag();
        this.networks.values().forEach(n -> {
            CompoundTag networkTag = new CompoundTag();
            networkTag.putString("type", n.getType().toString());
            networks.add(n.writeToNbt(networkTag));
        });
        tag.put("networks", networks);

        return tag;
    }
}
