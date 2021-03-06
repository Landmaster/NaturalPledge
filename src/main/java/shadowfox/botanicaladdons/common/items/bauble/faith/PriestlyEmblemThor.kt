package shadowfox.botanicaladdons.common.items.bauble.faith

import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.DamageSource
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import shadowfox.botanicaladdons.api.item.IPriestlyEmblem
import shadowfox.botanicaladdons.api.item.IWeightEnchantable
import shadowfox.botanicaladdons.api.priest.IFaithVariant
import shadowfox.botanicaladdons.common.enchantment.EnchantmentWeight
import shadowfox.botanicaladdons.common.lib.LibNames
import shadowfox.botanicaladdons.common.potions.ModPotions
import shadowfox.botanicaladdons.common.potions.base.ModPotionEffect
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.Vector3

/**
 * @author WireSegal
 * Created at 8:09 PM on 4/14/16.
 */
class PriestlyEmblemThor : IFaithVariant {

    override fun getName(): String = "thor"

    override fun hasSubscriptions(): Boolean = true

    override fun getSpells(stack: ItemStack, player: EntityPlayer): MutableList<String> {
        return mutableListOf(LibNames.SPELL_LIGHTNING, LibNames.SPELL_STRENGTH, LibNames.SPELL_PULL, LibNames.SPELL_THUNDER_TRAP, LibNames.SPELL_THOR_INFUSION)
    }

    override fun punishTheFaithless(stack: ItemStack, player: EntityPlayer) {
        player.addPotionEffect(ModPotionEffect(ModPotions.overcharged, 600))
    }

    @SubscribeEvent
    fun updatePlayerStep(e: LivingEvent.LivingUpdateEvent) {
        val player = e.entityLiving
        if (player is EntityPlayer) {
            val emblem = ItemFaithBauble.getEmblem(player, PriestlyEmblemThor::class.java) ?: return
            if ((player.onGround || player.capabilities.isFlying) && player.moveForward > 0F && !player.isInsideOfMaterial(Material.WATER))
                if (ManaItemHandler.requestManaExact(emblem, player, 1, true))
                    player.moveRelative(0.0f, 1.0f, 0.035f * if ((emblem.item as IPriestlyEmblem).isAwakened(emblem)) 1.5f else 1f)
        }
    }

    @SubscribeEvent
    fun onHitBySomething(e: LivingAttackEvent) {
        val player = e.entityLiving
        if (player is EntityPlayer) {
            val emblem = ItemFaithBauble.getEmblem(player, PriestlyEmblemThor::class.java) ?: return
            if (e.source == DamageSource.lightningBolt) {
                if (e.amount != 0f && (emblem.item as IPriestlyEmblem).isAwakened(emblem)) {
                    e.isCanceled = true
                    player.attackEntityFrom(e.source, 0f)
                } else if (e.amount > 4f && ManaItemHandler.requestManaExact(emblem, player, 10, true)) {
                    e.isCanceled = true
                    player.attackEntityFrom(e.source, 4f)
                }
            }
        }
    }

    fun isHeavyWeapon(stack: ItemStack): Boolean {
        return stack.item.getToolClasses(stack).contains("axe") || (stack.item is IWeightEnchantable && EnchantmentWeight.getWeight(stack) > 2)
    }

    @SubscribeEvent
    fun onHitSomething(e: AttackEntityEvent) {
        val emblem = ItemFaithBauble.getEmblem(e.entityPlayer, PriestlyEmblemThor::class.java) ?: return
        val stackInHand = e.entityPlayer.heldItemMainhand

        if (stackInHand != null && isHeavyWeapon(stackInHand) && e.target is EntityLivingBase) {
            if (ManaItemHandler.requestManaExact(emblem, e.entityPlayer, 10, true)) {
                Botania.proxy.lightningFX(Vector3.fromEntityCenter(e.entityPlayer), Vector3.fromEntityCenter(e.target), 1f, 0x00948B, 0x00E4D7)
                if (!e.entityPlayer.worldObj.isRemote)
                    (e.target as EntityLivingBase).addPotionEffect(PotionEffect(MobEffects.SLOWNESS, 100, 3, true, false))
            }
        }
    }
}
