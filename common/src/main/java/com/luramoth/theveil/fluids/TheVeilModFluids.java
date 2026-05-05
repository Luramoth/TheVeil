package com.luramoth.theveil.fluids;

import com.luramoth.theveil.TheVeilMod;
import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class TheVeilModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(TheVeilMod.MOD_ID, Registries.FLUID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(TheVeilMod.MOD_ID, Registries.BLOCK);

    public static final ArchitecturyFluidAttributes VEILFLUID_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(
                    () -> TheVeilModFluids.VEILFLUID_SOURCE,
                    () -> TheVeilModFluids.VEILFLUID_FLOWING
            )
            .block(TheVeilModFluids.VEILFLUID_BLOCK)
            .color(0xFF130134)
            .density(2500)
            .viscosity(4500)
            .slopeFindDistance(2)
            .dropOff(2);

    // fluid source
    public static final RegistrySupplier<FlowingFluid> VEILFLUID_SOURCE = FLUIDS.register("veilfluid",
            () -> new ArchitecturyFlowingFluid.Source(VEILFLUID_ATTRIBUTES));
    // the fluid itself (flowing)
    public static final RegistrySupplier<FlowingFluid> VEILFLUID_FLOWING = FLUIDS.register("flowing_veilfluid",
            () -> new ArchitecturyFlowingFluid.Flowing(VEILFLUID_ATTRIBUTES));
    // the physical block
    public static final RegistrySupplier<LiquidBlock> VEILFLUID_BLOCK = BLOCKS.register("veilfluid_block",
            () -> new ArchitecturyLiquidBlock(VEILFLUID_SOURCE, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
}
