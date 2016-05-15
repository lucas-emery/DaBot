package com.rangler.DiscordBot;

import java.util.EnumSet;

import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.*;

public class EventListener {
	
	@EventSubscriber
	public void onReady(ReadyEvent event) {
      
		System.out.println("Bot ready");	
	}
	
	
	@EventSubscriber
	public void commandListener(MessageReceivedEvent event){
		
		try {
			String _content = event.getMessage().getContent();

			if (!_content.startsWith("!"))
			   return;
			
			event.getMessage().delete();
			
			String _command = _content.toLowerCase();
			String[] _args = null;

			if (_content.contains(" "))
			{
			    _command = _command.split(" ")[0];
			    _args = _content.substring(_content.indexOf(' ') + 1).split(" ");
			}
			
			
			System.out.println("Command: "+_command);
			
			if(_command.equals("!help"))
			{
				String menu = "Available commands:\n"
							+ "   !help\n"
							+ "   !say <message>\n"
							+ "   !private <channelName>\t|Creates private voice channel\n"
							+ "   !add <user>\t\t\t\t\t\t\t|Gives user permission to join private channel(Caps insensitive)";
				new MessageBuilder(App.client).withChannel(event.getMessage().getChannel()).withContent(menu).build();
			}
			else if(_command.equals("!say"))
			{
				if(_args != null)
				{
					String text = "";
					for(int i = 0; i<_args.length; i++)
						text = text +" "+ _args[i];
						
					new MessageBuilder(App.client).withChannel(event.getMessage().getChannel()).withContent(text).build();
				}
			}
			else if(_command.equals("!private"))
			{
				if(_args != null)
				{
					//TODO INVERT WHEN UPDATED
					IVoiceChannel channel = event.getMessage().getGuild().createVoiceChannel(_args[0]);
					channel.overrideRolePermissions(event.getMessage().getGuild().getEveryoneRole(),EnumSet.of(Permissions.VOICE_CONNECT), EnumSet.noneOf(Permissions.class));
					channel.overrideUserPermissions(event.getMessage().getAuthor(), EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.VOICE_CONNECT));
					
					event.getMessage().getAuthor().moveToVoiceChannel(channel);
				}
			}
			else if(_command.equals("!add"))
			{
				if(_args != null)
				{
					if(!event.getMessage().getAuthor().getConnectedVoiceChannels().isEmpty())
					{
						IVoiceChannel channel = event.getMessage().getAuthor().getConnectedVoiceChannels().get(0);
						if(!channel.getModifiedPermissions(event.getMessage().getGuild().getEveryoneRole()).equals(event.getMessage().getGuild().getEveryoneRole().getPermissions()))
						{
							for(IUser user : event.getMessage().getGuild().getUsers())
							{
								if(user.getDisplayName(event.getMessage().getGuild()).toLowerCase().equals(_args[0].toLowerCase()))
								{
									channel.overrideUserPermissions(user, EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.VOICE_CONNECT));
									break;
								}
							}
						}
						else
							new MessageBuilder(App.client).withChannel(event.getMessage().getChannel()).withContent(event.getMessage().getAuthor().mention()+" this channel isn't private").build();
					}
					else
						new MessageBuilder(App.client).withChannel(event.getMessage().getChannel()).withContent(event.getMessage().getAuthor().mention()+" you aren't connected to any voice channel").build();
				}
			}
			else
			{
				String menu = "Available commands:\n"
						+ "   !help\n"
						+ "   !say <message>\n"
						+ "   !private <channelName>\t|Creates private voice channel\n"
						+ "   !add <user>\t\t\t\t\t\t\t|Gives user permission to join private channel(Caps insensitive)";
				new MessageBuilder(App.client).withChannel(event.getMessage().getChannel()).withContent(_command +" is not a valid command\n\n"+ menu).build();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventSubscriber
	public void deletePrivateChannel(UserVoiceChannelLeaveEvent event){
		
		if(event.getChannel().getConnectedUsers().isEmpty())
		{
			if(!event.getChannel().getModifiedPermissions(event.getChannel().getGuild().getEveryoneRole()).equals(event.getChannel().getGuild().getEveryoneRole().getPermissions()) && !event.getChannel().getID().equals("179758170619641857"))
			{
				try {
					event.getChannel().delete();
				} catch (MissingPermissionsException | HTTP429Exception | DiscordException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@EventSubscriber
	public void onUserChangedChannel(UserVoiceChannelMoveEvent event){
		App.client.getDispatcher().dispatch(new UserVoiceChannelLeaveEvent(event.getUser(), event.getOldChannel()));
	}
	
	
}


