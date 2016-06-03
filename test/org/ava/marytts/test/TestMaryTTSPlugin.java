package org.ava.marytts.test;

import org.ava.marytts.MaryTTSPlugin;

public class TestMaryTTSPlugin {

	public static void main(String[] args) {
		MaryTTSPlugin mtp = new MaryTTSPlugin();
		mtp.start();
		mtp.sayText("Hello world!");
		sleep(500);
		mtp.sayText("Hi it's me again!");
		sleep(900);
		mtp.sayText("The fight for control over Sumner M. Redstone’s $40 billion media empire"
				+ " has escalated significantly. "
				+ "In an unexpected development, Mr. Redstone on Friday removed Philippe P. Dauman,"
				+ " the chief executive of Viacom, from the trust that will control his companies"
				+ " after he dies or is declared incompetent. Mr. Redstone also removed George Abrams,"
				+ " a longtime director at Viacom."
				+ "Mr. Dauman and Mr. Abrams were also removed from the board of National Amusements,"
				+ " the private theater chain company through which Mr. Redstone, 92 and in frail health,"
				+ " controls his media empire."
				+ "Early Saturday morning, Mr. Redstone confirmed the news through a statement"
				+ " from Michael C. Tu, a partner at the law firm Orrick, Herrington & Sutcliffe.");
		mtp.stop();
	}
	
	public static void sleep(int time) {
		try {
			System.out.println("Sleep");
			Thread.sleep(time);
			System.out.println("wakeup");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
