package de.tr7zw.tas;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TAS {

	private Minecraft mc = Minecraft.getMinecraft();
	private boolean loaded = false;
	private ArrayList<KeyFrame> keyFrames = new ArrayList<>();
	private int line = 0;

	public void loadData(File tasData){
		loaded = true;
		keyFrames = new ArrayList<>();
		line = 0;
		try{
			try (Stream<String> stream = Files.lines(tasData.toPath())) {
				stream.forEach(s -> parseLine(s, ++line));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//keyFrames.add(new KeyFrame(false, false, false, false, false, false, 0, 0, false, false));
	}

	public void clearData(){
		if(mc.player != null){
			mc.player.movementInput = new MovementInputFromOptions(mc.gameSettings);
		}
		mc.gameSettings.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
		mc.gameSettings.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
		loaded = false;
		keyFrames = new ArrayList<>();
	}

	public static void sendMessage(String msg){
		try{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(msg));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void parseLine(String line, int lineid){
		if(line.startsWith("#") || line.startsWith("//"))return;//Comments
		String[] args = line.split(";");
		int repeats = 1;
		try{
			repeats = Integer.parseInt(args[0]);
		}catch(Exception ex){}
		try{
			KeyFrame frame = new KeyFrame(Boolean.parseBoolean(args[1]), //up
					Boolean.parseBoolean(args[2]), //down
					Boolean.parseBoolean(args[3]), //left
					Boolean.parseBoolean(args[4]), //right
					Boolean.parseBoolean(args[5]), //jump
					Boolean.parseBoolean(args[6]), //sneak
					Float.parseFloat(args[7]), //pitch
					Float.parseFloat(args[8]), //yaw
					Boolean.parseBoolean(args[9]), //leftclick
					Boolean.parseBoolean(args[10]),
					Boolean.parseBoolean(args[11])); //rightclick
			for(int i = 0; i < repeats; i++)
				keyFrames.add(frame);
		}catch(Exception ex){
			System.err.println("Error parsing line " + lineid);
			sendMessage("Error parsing line " + lineid);
			ex.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.PlayerTickEvent ev)
	{
		//if(ev.phase == Phase.START && loaded && mc.player != null && !(mc.player.movementInput instanceof TASInput))
		//	mc.player.movementInput = new TASInput(this, keyFrames);
		if(ev.phase == Phase.START && mc.player != null && mc.player.movementInput instanceof TASInput){
			TASInput input = (TASInput) mc.player.movementInput;
			if(input.donePlaying){
				clearData();
			}
		}
	}

	@SubscribeEvent()
	public void onMenu(net.minecraftforge.client.event.GuiOpenEvent ev)
	{
		if(ev.getGui() instanceof GuiMainMenu){
			clearData();
		}
	}

	public Recorder recorder = null;

	@SubscribeEvent
	public void onChatSend(ServerChatEvent ev)
	{
		if(ev.getMessage().equals(".record")){
			ev.setCanceled(true);
			if(recorder != null){
				sendMessage("A recording is running!");
				return;
			}
			if(mc.player.movementInput instanceof TASInput){
				sendMessage("A record is playing!");
				return;
			}
			sendMessage("Starting the tas recording!");
			recorder = new Recorder();
			MinecraftForge.EVENT_BUS.register(recorder);
			return;
		}
		if(ev.getMessage().equals(".stoprecord")){
			ev.setCanceled(true);
			if(recorder == null){
				sendMessage("No recording running!");
				return;
			}
			sendMessage("Stopped the tas recording!");
			MinecraftForge.EVENT_BUS.unregister(recorder);
			//SAVE
			File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
					Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + "recording_" + System.currentTimeMillis() +".tas");
			recorder.saveData(file);
			recorder = null;
			return;
		}
		if(ev.getMessage().startsWith(".play")){
			String[] args = ev.getMessage().split(" ");
			if(args.length != 2){
				sendMessage("Example: .play filename     (without .tas)");
				return;
			}
			ev.setCanceled(true);
			if(Minecraft.getMinecraft().getIntegratedServer() != null && !loaded){
				File file = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + 
						Minecraft.getMinecraft().getIntegratedServer().getFolderName() + File.separator + args[1] + ".tas");
				if(file.exists()){
					loadData(file);
					mc.player.movementInput = new TASInput(this, keyFrames);
					sendMessage("Loaded File");
				}else{
					sendMessage("File not found: " + file.getAbsolutePath());
				}
			}
		}
	}


}
