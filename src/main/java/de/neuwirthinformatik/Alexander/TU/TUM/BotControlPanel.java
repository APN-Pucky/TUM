package de.neuwirthinformatik.Alexander.TU.TUM;

import java.awt.BorderLayout;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import de.neuwirthinformatik.Alexander.TU.TUM.BOT.Bot;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.BattleControlPanel;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.BrawlControlPanel;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.CampaignControlPanel;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.ConquestControlPanel;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.GuildQuestControlPanel;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.MissionControlPanel;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.PracticeControlPanel;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.RaidControlPanel;
import de.neuwirthinformatik.Alexander.TU.TUM.Botting.WarControlPanel;
import de.neuwirthinformatik.Alexander.TU.util.GUI;
import de.neuwirthinformatik.Alexander.TU.util.Task;

public class BotControlPanel extends JPanel {

	public static GUI.IntegerField d_mins;
	public static GUI.IntegerField d_hours;
	public static GUI.IntegerField l_mins;
	public static GUI.IntegerField l_hours;
	public static GUI.IntegerField l_times;
	public static JCheckBox l_infinite;
	public static GUI.IntegerField t_num;
	public static JCheckBox t_infinite;
	
	public BotControlPanel() {
		setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		if(Permissions.has("control_mission"))tabbedPane.addTab("Mission", null, new JScrollPane(new MissionControlPanel()),      "Mission");
		if(Permissions.has("control_battle"))tabbedPane.addTab("Battle", null, new JScrollPane(new BattleControlPanel()),   "Battle");
		if(Permissions.has("control_guildquest"))tabbedPane.addTab("Guild Quest", null, new JScrollPane(new GuildQuestControlPanel()),   "Guild Quest");
		if(Permissions.has("control_brawl"))tabbedPane.addTab("Brawl", null, new JScrollPane(new BrawlControlPanel()),  "Brawl");
		if(Permissions.has("control_war"))tabbedPane.addTab("War", null, new JScrollPane(new WarControlPanel()),"War");
		if(Permissions.has("control_conquest"))tabbedPane.addTab("Conquest", null, new JScrollPane(new ConquestControlPanel()),  "Conquest");
		if(Permissions.has("control_campaign"))tabbedPane.addTab("Campaign", null, new JScrollPane(new CampaignControlPanel()),  "Campaign");
		if(Permissions.has("control_raid"))tabbedPane.addTab("Raid", null, new JScrollPane(new RaidControlPanel()),  "Raid");
		if(Permissions.has("control_practice"))tabbedPane.addTab("Practice", null, new JScrollPane(new PracticeControlPanel()),  "Practice");
		
		JPanel panel = new JPanel();
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
			p.add(GUI.textSmall("dump-delay: "));
			p.add(GUI.label("hours: "));
			p.add(d_hours = GUI.numericEdit(0));
			p.add(GUI.label("minutes: "));
			p.add(d_mins = GUI.numericEdit(0));
		box.add(p);
		box.add(new JSeparator());
		
		
		
		p = new JPanel();
			p.add(GUI.textSmall("loop-wait: "));
			p.add(GUI.label("hours: "));
			p.add(l_hours = GUI.numericEdit(0));
			p.add(GUI.label("minutes: "));
			p.add(l_mins = GUI.numericEdit(0));
		box.add(p);
		
		p = new JPanel();
			p.add(GUI.textSmall("loop-times: "));
			p.add(GUI.label("repeat-times:  "));
			p.add(l_times = GUI.numericEdit(1));
			p.add(l_infinite = GUI.check("infinite", false));
			p.add(new JPanel());
		box.add(p);
		p = new JPanel();
			p.add(GUI.textSmall("loop-split: "));
			p.add(GUI.label("thread-num:     "));
			p.add(t_num = GUI.numericEdit(1));
			p.add(t_infinite = GUI.check("infinite", false));
			p.add(new JPanel());
		box.add(p);
		
		panel.add(box);
		
		JSplitPane splitPane= new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                tabbedPane,panel);
		add(splitPane,BorderLayout.CENTER);
	}
	
	public static boolean waitpredump(String name)
	{
		int min = d_mins.getNumber() + 60* d_hours.getNumber();
		if(min>0)return TUM.pp.wait("Wait for " + name + " dump", min);
		return false;
	}
	
	public static boolean waitloop(String name, int mins, int hours)
	{
		int min = mins + 60* hours;
		if(min>0)return TUM.pp.wait("Sleeping for " + name + " loop", min);
		return false;
	}
	
	//TODO register as a task?!?
	//TODO mass parallel task synchronized
	public static <T> void dumpBotsSingle(T[] bots, String l_title, int times, BiFunction<T,Integer,String> dump)
	{
		int i = 1;
		IntFunction<String> update = (j) -> j+1 +". dump done";
		//boolean canceled = false;
		
		boolean loop_inf = l_infinite.isSelected();
		int loop_times = l_times.getNumber();
		int loop_hours = l_hours.getNumber();
		int loop_mins = l_mins.getNumber();
		if(waitpredump(l_title))return;

		if(TUM.pp.forEachDouble(l_title, bots, (b,j) -> b.toString() + ": " + dump.apply(b, j) + "", "Repeat " + l_title + " " + i + "/"+ (loop_inf?"inf":loop_times),times, update))return;
		while(i <loop_times || loop_inf)
		{
			i++;
			if(waitloop(l_title +" #" + i, loop_mins, loop_hours))return;
			if(TUM.pp.forEachDouble(l_title, bots, (b,j) -> b.toString() + ": " + dump.apply(b, j) + "", "Repeat " + l_title + " " + i + "/"+ (loop_inf?"inf":loop_times),times, update))return;
		}
	}
	public static void dumpBotsSingle(String l_title, int times, BiFunction<Bot,Integer,String> dump)
	{
		Bot[] bots = TUM._this.botovp.getSelectedBots();
		dumpBotsSingle(bots,l_title,times,dump);
	}
	
	public static void dumpBotsParallel(String l_title, int times, Function<Bot,String> dump)
	{
		Bot[] bots = TUM._this.botovp.getSelectedBots();
		BiFunction<Bot,Integer,String> bidump = (b,j)->dump.apply(b);
		int group = (int) (t_infinite.isSelected()?1:(Math.ceil((double)bots.length)/t_num.getNumber()));
		for(int i=0; i < bots.length;i+=group)
		{
			Bot[] g = new Bot[(i+group>bots.length)?bots.length-i:group];
			System.arraycopy(bots, i, g, 0, g.length);
			Task.start(() ->dumpBotsSingle(g,l_title,times,bidump));;
		}
	}

}
