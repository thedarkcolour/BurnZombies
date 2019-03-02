package thedarkcolour.burnzombies;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = BurnZombies.MODID, name = BurnZombies.NAME, version = BurnZombies.VERSION)
public class BurnZombies {
    static final String MODID = "burnzombies";
    static final String NAME = "BurnZombiesWithHelmets";
    static final String VERSION = "1.0";

    public static Configuration config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(Configuration.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(burnZombieEvent.class);
        ConfigManager.sync(MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
    }

    @Mod.EventBusSubscriber
    public static class burnZombieEvent {
        @SubscribeEvent
        public static void burnZombie(LivingEvent.LivingUpdateEvent event) {
            EntityLivingBase entityLivingBase = event.getEntityLiving();

            World world = entityLivingBase.getEntityWorld();
            if(entityLivingBase.isEntityUndead() && !world.isRemote) {

                if(!entityLivingBase.isWet() && world.canSeeSky(new BlockPos(entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.getEyeHeight(), entityLivingBase.posZ))) {
                    ItemStack itemStack = entityLivingBase.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

                    if(itemStack != ItemStack.EMPTY && !entityLivingBase.isBurning()) {
                        entityLivingBase.setFire(8);
                    }
                    else if (Configuration.burnBabyZombies && entityLivingBase instanceof EntityZombie) {
                        EntityZombie zombie = (EntityZombie) entityLivingBase;

                        if(zombie.isChild() && !zombie.isBurning()) {
                            zombie.setFire(8);
                        }
                    }
                }
            }
        }
    }

    @Config(modid = BurnZombies.MODID, name = BurnZombies.MODID)
    public static class Configuration {
        @SubscribeEvent
        public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
            ConfigManager.sync(BurnZombies.MODID, Config.Type.INSTANCE);
        }

        @Config.Name("Burn baby zombies")
        @Config.Comment("If true, also burns baby zombies.")
        public static boolean burnBabyZombies = false;
    }
}
