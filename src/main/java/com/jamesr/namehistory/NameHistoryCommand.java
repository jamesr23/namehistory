package com.jamesr.namehistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class NameHistoryCommand extends CommandBase {

	private List<String> aliases;

    public NameHistoryCommand(){
        aliases  = new ArrayList<String>();
		aliases.add("nh");
    }

    @Override
	public String getCommandName() {
		return "namehistory";
	}

	@Override
	public List<String> getCommandAliases() {
		return aliases;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return null;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if (args.length < 1) {
			sender.addChatMessage(new ChatComponentText("[namehistory] "  + EnumChatFormatting.RED + "error: " + EnumChatFormatting.WHITE + "enter a username"));
			return;
		}

		Thread t = new Thread(new RunnableThread(sender, args[0]).getRunnable());
        // Thread t = new Thread(() -> getNames(sender, args[0])); // lambdas not supported
		t.start();

		return;
	}

	private class RunnableThread {
		private ICommandSender sender;
		private String username;
		public RunnableThread(ICommandSender sender, String username) {
			this.sender = sender;
			this.username = username;

		}
		public Runnable getRunnable() {
			return new Runnable() {
                @Override
				public void run() {
					try {
						getNames(sender, username);
					} catch (IOException e) {
						sender.addChatMessage(new ChatComponentText("[namehistory]" + EnumChatFormatting.RED + "error:" + EnumChatFormatting.WHITE + " IOException or MalformedURLException"));
						e.printStackTrace();
					}
				}
			};
		}
	}

	private static void getNames(ICommandSender sender, String username) throws IOException, MalformedURLException {
		
		sender.addChatMessage(new ChatComponentText("[namehistory] Fetching name history for " + EnumChatFormatting.BLUE + username));

		String uuidResponse = URLToString(new URL("https://api.mojang.com/users/profiles/minecraft/" + username));
		if (uuidResponse == null) {
			sender.addChatMessage(new ChatComponentText("error: " + username + " doesn't exist or an error occured"));
			return;
		}

		String uuid = new JsonParser()
				.parse(uuidResponse)
				.getAsJsonObject()
				.get("id")
				.getAsString();
		
		JsonArray names = new JsonParser()
				.parse(URLToString(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names")))
				.getAsJsonArray();

		for (int i = 0; i < names.size(); i++) {
			sender.addChatMessage(new ChatComponentText(" " + (i + 1) + ": " + names.get(i).getAsJsonObject().get("name").getAsString()));
		}
		return;
	}

	private static String URLToString(URL url) throws IOException {
		URLConnection connection= url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String string = reader.readLine();
		reader.close();
		return string;
	}
	
}
