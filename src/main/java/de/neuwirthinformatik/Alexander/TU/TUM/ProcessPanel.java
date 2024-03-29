package de.neuwirthinformatik.Alexander.TU.TUM;

import java.awt.Component;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import de.neuwirthinformatik.Alexander.TU.util.GUI;
import de.neuwirthinformatik.Alexander.TU.util.GUI.LoadBar;
import de.neuwirthinformatik.Alexander.TU.util.Task;

public class ProcessPanel extends JPanel {
	JPanel main_panel;
	public ProcessPanel()
	{
		//super(new GridLayout(1,0));
		main_panel = new JPanel();
		main_panel.setLayout(new BoxLayout(main_panel,BoxLayout.Y_AXIS));
		main_panel.add(GUI.label("Processes"));
		JPanel jp = new JPanel();
		jp.add(GUI.buttonSync("min all", () -> min_all()));
		jp.add(GUI.buttonSync("cancel all", () -> cancel_all()));
		main_panel.add(jp);
		main_panel.add(new JSeparator());
		
		add(main_panel);
	}
	
	public void task()
	{
		
	}
	
	public boolean wait(String title, int min) {
		Integer[] arr = new Integer[min];
		for (int i = 0; i < min; i++)
			arr[i] = i;
		return forEach(title, arr, (t, j) -> {
			Task.sleep(60000);
			return "";
		}); // true if canceled
	}

	public void cancel_all()
	{
		for(Component c : main_panel.getComponents())
		{
			if(c instanceof JPanel)
			{
				for(Component k : ((JPanel)c).getComponents())
				{
					if(k instanceof JButton && ((JButton)k).getText().equals("cancel"))
					{
						((JButton)k).doClick();
					}
				}
			}
		}
	}
	
	public void min_all()
	{
		for(Component c : main_panel.getComponents())
		{
			if(c instanceof JPanel)
			{
				for(Component k : ((JPanel)c).getComponents())
				{
					if(k instanceof JButton && ((JButton)k).getText().equals("minimize"))
					{
						((JButton)k).doClick();
					}
				}
			}
		}
	}
	
	public <T> boolean forEach(String title, T[] arr, Function<T,String> action)
	{
		return forEach(title, arr,(t,j) -> {return action.apply(t);});
	}
	
	public <T> boolean forEach(String title, T[] arr, BiFunction<T,Integer,String> action)
	{
		GUI.LoadBar bar = new LoadBar(title, arr.length,TUM.settings.PROCESS_WINDOWS);
		
		add(bar);
		
		int j =0;
		for(int i = 0; i < arr.length;i++)
		{
			String msg ="";
			if(arr[i] !=null)
			{
				try {
					msg = action.apply(arr[i],j);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					msg = "Error";
				}
				j++;
			}
			bar.setProgress(i,msg);
			if(bar.isCanceled())break;
		}
		boolean ret = bar.isCanceled();
		bar.close();
		remove(bar);
		return ret;
	}
	
	public <T> boolean forEachDouble(String l_title, T[] arr, BiFunction<T,Integer,String> l_action, 
			String h_title, int times,IntFunction<String> h_update)
	{
		GUI.LoadBar l_bar = new LoadBar(l_title + " " + "1/" + times, arr.length,TUM.settings.PROCESS_WINDOWS);
		GUI.LoadBar h_bar = new LoadBar(h_title, times,TUM.settings.PROCESS_WINDOWS);
		
		add(l_bar,h_bar);
		
		for(int k = 0; k < times;k++)
		{	
			int j =0;
			for(int i = 0; i < arr.length;i++)
			{
				String msg ="";
				if(arr[i] !=null)
				{
					try {
						msg = l_action.apply(arr[i],j);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						msg = "Error";
					}
					j++;
				}
				l_bar.setProgress(i, msg);
				if(l_bar.isCanceled())break;
			}
			l_bar.reset();
			l_bar.setHeader(l_title + " " + (k+1) + "/" + times);
			h_bar.setProgress(k, h_update.apply(k));
			if(l_bar.isCanceled() || h_bar.isCanceled())break;
		}
		boolean ret = l_bar.isCanceled() || h_bar.isCanceled();
		l_bar.close();
		h_bar.close();
		
		remove(l_bar,h_bar);
		return ret; //true == cancled, else fine
	}
	
	private HashMap<GUI.LoadBar,JSeparator> hm = new HashMap<GUI.LoadBar, JSeparator>();
	
	private void add(GUI.LoadBar ... bars)
	{
		for (GUI.LoadBar l : bars)
		{
			main_panel.add(l.getInlinePanel());
		}

		JSeparator sep = new JSeparator();
		hm.put(bars[0], sep);
        main_panel.add(sep);
		main_panel.revalidate();
		main_panel.repaint();
	}
	
	private void remove(GUI.LoadBar ... bars)
	{
		for (GUI.LoadBar l : bars)
		{
			main_panel.remove(l.getInlinePanel());
		}

		JSeparator sep = hm.get(bars[0]);
		hm.remove(bars[0]);
        main_panel.remove(sep);
		main_panel.revalidate();
		main_panel.repaint();
	}
}
