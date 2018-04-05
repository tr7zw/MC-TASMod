package de.tr7zw.tas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.io.Files;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class Recorder {

	private ArrayList<Object> recording = new ArrayList<>();
	private Minecraft mc = Minecraft.getMinecraft();
	
	public Recorder() {
		recording.add("#StartLocation: " + mc.player.getPosition().toString());
		mc.player.movementInput = new RecordingInput(mc.gameSettings, recording);
	}
	
	public void saveData(File file){
		mc.player.movementInput = new MovementInputFromOptions(mc.gameSettings);
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < recording.size(); i++){
			Object o = recording.get(i);
			if(o instanceof String){
				output.append(o + "\n");
			}else if(o instanceof KeyFrame){
				KeyFrame frame = (KeyFrame) o;
				output.append("1;" + frame.forwardKeyDown + ";" + frame.backKeyDown + ";" + frame.leftKeyDown + ";" + frame.rightKeyDown + ";"
						+ frame.jump + ";" + frame.sneak + ";" + frame.pitch + ";" + frame.yaw + ";" + frame.leftClick + ";" + frame.rightClick
						+ ";" + frame.sprint + ";\n");
			}
		}
		try {
			Files.write(output.toString().replaceAll("false", " ").getBytes(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("Saved to: " + file.getAbsolutePath()));
		}catch(Exception exX){
			exX.printStackTrace();
		}
	}
	
	private static class RecordingInput extends MovementInputFromOptions{

		ArrayList<Object> recording;
		Minecraft mc = Minecraft.getMinecraft();
		
		public RecordingInput(GameSettings p_i1237_1_, ArrayList<Object> recording) {
			super(p_i1237_1_);
			this.recording = recording;
		}

		@Override
		public void updatePlayerMoveState() {
			super.updatePlayerMoveState();
			MovementInput input = this;
			recording.add(new KeyFrame(input.forwardKeyDown, input.backKeyDown, input.leftKeyDown, input.rightKeyDown, input.jump, input.sneak,
					mc.player.rotationPitch, mc.player.rotationYaw, GameSettings.isKeyDown(mc.gameSettings.keyBindAttack),
					GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem), GameSettings.isKeyDown(mc.gameSettings.keyBindSprint)));
		}
		
	}
	
}
