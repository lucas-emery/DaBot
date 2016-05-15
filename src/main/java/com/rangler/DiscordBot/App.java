package com.rangler.DiscordBot;

import sx.blah.discord.api.*;
import sx.blah.discord.util.*;

public class App 
{
	static IDiscordClient client;
	
    public static void main( String[] args ) throws DiscordException
    {    	
        client = new ClientBuilder().withToken("MTgwODAyMzY1NzU3MDYzMTY4.Chfg0w.Vtm7s3KTU0kq7mrn5CXs5dpxkrA").login();
        
        client.getDispatcher().registerListener(new EventListener());
        System.out.println("Dispatchers ready");
    }
}
