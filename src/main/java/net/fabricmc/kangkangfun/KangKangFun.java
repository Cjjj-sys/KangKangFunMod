package net.fabricmc.kangkangfun;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class KangKangFun implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("kangkangfun");
	public static boolean limitRun = false;


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
		{

			/* 手动的旁观者检查是必要的，因为 AttackBlockCallbacks 会在旁观者检查之前应用 */
			if (!player.isSpectator())
			{
				limitRun = !limitRun;
				if (!limitRun) {
					int[] effectRawIdArray = {1, 2, 3, 4, 5, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 31};
					String[] effectNameArray = {"速度", "缓慢", "急迫", "挖掘疲劳", "力量", "跳跃提升", "反胃", "生命恢复", "抗性提升", "防火", "水下呼吸", "隐身", "失明", "夜视", "饥饿", "虚弱", "中毒", "凋零", "生命提升", "伤害吸收", "饱和", "不祥之兆"};
					int effectRawIdIndex = new Random().nextInt(0, 22);// [0,22)
					int duration = new Random().nextInt(25, 125);// [25,125) 间隔, 单位游戏刻
					int amplifier = new Random().nextInt(0, 3);// [0,3) 级别
					var newStatusEffect = StatusEffect.byRawId(effectRawIdArray[effectRawIdIndex]);

					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							var playerEffects = player.getStatusEffects();
							List<StatusEffect> toRemoveEffects = new ArrayList<>();
							for (StatusEffectInstance playerEffect: playerEffects) {
								if (playerEffect.getDuration() == 0) {
									toRemoveEffects.add(playerEffect.getEffectType());
								}
							}// 获取要删除的效果
							for (StatusEffect toRemoveEffect: toRemoveEffects) {
								player.removeStatusEffect(toRemoveEffect);
							}// 删除效果
						}
					}, duration * 50L + 100);// 100tick = 100 * 0.05 = 5s = 100 * 50 = 5000ms

					StatusEffectInstance statusEffectInstance = new StatusEffectInstance(newStatusEffect,
							duration,
							amplifier);
					//player.clearStatusEffects();
					player.addStatusEffect(statusEffectInstance);
					assert newStatusEffect != null;
					String newEffectName = effectNameArray[effectRawIdIndex];
					player.sendMessage(Text.of("接受你的礼物吧! " + (statusEffectInstance.getAmplifier() + 1) + "级别 "+statusEffectInstance.getDuration() * 0.05 + "秒 的 " + newEffectName + " 效果"), true);

				}

			}
			return ActionResult.PASS;
		});
	}
}
