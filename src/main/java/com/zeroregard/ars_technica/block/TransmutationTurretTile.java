package com.zeroregard.ars_technica.block;

import com.alexthw.sauce.common.block.FocusEnhancedSpellTurretTile;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.zeroregard.ars_technica.Config;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import static com.hollingsworth.arsnouveau.common.block.BasicSpellTurret.TURRET_BEHAVIOR_MAP;

public class TransmutationTurretTile extends FocusEnhancedSpellTurretTile {

    public TransmutationTurretTile(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return EntityRegistry.TRANSMUTATION_TURRET_BLOCK_ENTITY.get();
    }

    @Override
    public int getManaCost() {
        Spell spell = this.spellCaster.getSpell();
        double multiplier = Config.Common.TRANSMUTATION_TURRET_SOURCE_COST_MULTIPLIER.get();
        return (int) (spell.getCost() * multiplier);
    }

    @Override
    public void shootSpell() {
        BlockPos pos = this.getBlockPos();

        if (spellCaster.getSpell().isEmpty() || !(this.level instanceof ServerLevel world))
            return;
        int manaCost = getManaCost();
        if (manaCost > 0 && SourceUtil.takeSourceMultiple(pos, world, 10, manaCost) == null)
            return;
        Networking.sendToNearbyClient(world, pos, new PacketOneShotAnimation(pos));
        Position iposition = BasicSpellTurret.getDispensePosition(pos, world.getBlockState(pos).getValue(BasicSpellTurret.FACING));
        Direction direction = world.getBlockState(pos).getValue(BasicSpellTurret.FACING);
        FakePlayer fakePlayer = ANFakePlayer.getPlayer(world);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        
        var resolver = new TransmutationTurretSpellResolver(new SpellContext(world, spellCaster.getSpell(), fakePlayer, new TileCaster(this, SpellContext.CasterType.TURRET)));
        if (resolver.castType != null && TURRET_BEHAVIOR_MAP.containsKey(resolver.castType)) {
            TURRET_BEHAVIOR_MAP.get(resolver.castType).onCast(resolver, world, pos, fakePlayer, iposition, direction);
        }
    }

    static class TransmutationTurretSpellResolver extends EntitySpellResolver {

        public TransmutationTurretSpellResolver(SpellContext context) {
            super(context);
        }

        @Override
        public boolean hasFocus(ItemStack stack) {
            return hasFocus(stack.getItem());
        }

        @Override
        public boolean hasFocus(Item item) {
            if (item == ItemRegistry.TRANSMUTATION_FOCUS.get()) {
                return true;
            }
            return super.hasFocus(item);
        }

        @Override
        public SpellResolver getNewResolver(SpellContext context) {
            return new TransmutationTurretSpellResolver(context);
        }
    }
}