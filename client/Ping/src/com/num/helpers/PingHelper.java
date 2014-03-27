package com.num.helpers;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.num.models.Address;
import com.num.models.LastMile;
import com.num.models.Measure;
import com.num.models.Ping;
import com.num.models.WarmupExperiment;
import com.num.utils.CommandLineUtil;
import com.num.utils.ParseUtil;

public class PingHelper {

	public static CommandLineUtil cmdUtil;

	public static LastMile firstHopHelp(Address address, int count) {


		LastMile p 			= null;
		int ttl 		= 1;
		String ipDst 	= address.getIp();
		String cmd 		= "ping";
		String options 	= "-n -s 56 -c 1 -t " + ttl;
		String output 	= "";

		cmdUtil = new CommandLineUtil();
		int hopCount = 1;
		output = cmdUtil.runCommand(cmd, ipDst, options);

		try{

			if (!output.contains("ttl")) {
				if (!output.contains("From")){
					while(!output.contains("From")) {
						options 	= "-n -s 56 -c 1 -t " + ++ttl;
						output = cmdUtil.runCommand(cmd, ipDst, options);
						if (ttl > 50) {
							break;
						}
					}			
				}
				ipDst = output.substring(output.indexOf("From") + 4, output.indexOf("icmp")).trim();
				options 	= "-c 5";
				output = cmdUtil.runCommand(cmd, ipDst, options);
			}		
			Measure ping_measurement = ParseUtil.PingParser(output);

			Socket conn;
			String ipSrc = "";
			try {
				conn = new Socket("www.google.com", 80);
				ipSrc = conn.getLocalAddress().toString(); 
				conn.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			p=new LastMile(ipSrc, address ,ping_measurement, ttl, ipDst);
		}
		catch(Exception e){
			p=new LastMile("", address ,new Measure(-1, -1, -1, -1), ttl, ipDst);
		}
		

		return p;
	}

	/**
	 * Pinghelp helps run ping command by creating cmd and inputs
	 * @return
	 */
	public static Ping pingHelp(Address address, int count) {
		Ping p 			= null;
		String ipDst 	= address.getIp();
		String cmd 		= "ping";
		double timegap = 0.5;
		String options 	= "-c " + count +" -i "+timegap;
		String output 	= "";

		cmdUtil = new CommandLineUtil();

		output = cmdUtil.runCommand(cmd, ipDst, options);

		Measure ping_measurement = ParseUtil.PingParser(output);

		Socket conn;
		String ipSrc = "";
		try {
			conn = new Socket("www.google.com", 80);
			ipSrc = conn.getLocalAddress().toString();
			conn.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		p=new Ping(ipSrc, address ,ping_measurement);

		return p;
	}

	
	public static void warmupSequenceHelp(WarmupExperiment experiment) {
		Ping p 			= null;
		String ipDst 	= experiment.getAddress().getIp();
		String cmd 		= "ping";
		String options 	= "-c " + experiment.getTotal_count() + " -i " + experiment.getTime_gap();
		String output 	= "";

		cmdUtil = new CommandLineUtil();

		output = cmdUtil.runCommand(cmd, ipDst, options);
		
		ParseUtil.warmupParser(output,experiment);
 	}



}