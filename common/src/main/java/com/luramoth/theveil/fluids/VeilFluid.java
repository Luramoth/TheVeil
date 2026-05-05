package com.luramoth.theveil.fluids;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public  class VeilFluid {
    public static class Source extends ArchitecturyFlowingFluid.Source {

        public Source(ArchitecturyFluidAttributes attributes) {
            super(attributes);
            this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 8));
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends ArchitecturyFlowingFluid.Flowing {
        public Flowing(ArchitecturyFluidAttributes attributes) {
            super(attributes);
        }
    }
}
