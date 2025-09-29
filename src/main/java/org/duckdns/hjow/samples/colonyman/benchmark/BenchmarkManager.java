package org.duckdns.hjow.samples.colonyman.benchmark;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.duckdns.hjow.samples.util.UIUtil;

public class BenchmarkManager {
    protected JDialog dialog;
    protected CardLayout card;
    protected JPanel pnCard, pnArena;
    protected JButton btnStart;
    protected JComboBox<String> cbxThread, cbxCycle;
    protected JTextArea taFront, taResult;
    
    protected List<BenchmarkThread> list = new Vector<BenchmarkThread>();
    protected List<JPanel>          pns  = new Vector<JPanel>();
    protected List<JProgressBar>    bars = new Vector<JProgressBar>();
    
    protected volatile boolean threadSwitch = true;
    protected volatile boolean threadPaused = true;
    
    public BenchmarkManager(JDialog superDialog) {
    	dialog = new JDialog(superDialog);
    	dialog.setSize(600, 500);
    	UIUtil.center(dialog);
    	dialog.setTitle("Benchmark Manager");
    	dialog.addWindowListener(new WindowAdapter() {
    		@Override
    		public void windowClosing(WindowEvent e) {
    			dispose();
    		}
		});
    	
    	dialog.setLayout(new BorderLayout());
    	
    	JPanel pnMain = new JPanel();
    	pnMain.setLayout(new BorderLayout());
    	dialog.add(pnMain, BorderLayout.CENTER);
    	
    	pnCard = new JPanel();
    	card = new CardLayout();
    	pnCard.setLayout(card);
    	pnMain.add(pnCard, BorderLayout.CENTER);
    	
    	JPanel pnCard1, pnCard2, pnCard3;
    	pnCard1 = new JPanel();
    	pnCard2 = new JPanel();
    	pnCard3 = new JPanel();
    	pnCard1.setLayout(new BorderLayout());
    	pnCard2.setLayout(new BorderLayout());
    	pnCard3.setLayout(new BorderLayout());
    	pnCard.add(pnCard1, "C1");
    	pnCard.add(pnCard2, "C2");
    	pnCard.add(pnCard3, "C3");
    	
    	JPanel pnUp, pnCenter, pnDown;
    	pnUp     = new JPanel();
    	pnCenter = new JPanel();
    	pnDown   = new JPanel();
    	pnCard1.add(pnUp    , BorderLayout.NORTH);
    	pnCard1.add(pnCenter, BorderLayout.CENTER);
    	pnCard1.add(pnDown  , BorderLayout.SOUTH);
    	
    	pnUp.setLayout(new BorderLayout());
    	
    	taFront = new JTextArea();
    	taFront.setEditable(false);
    	pnUp.add(taFront, BorderLayout.CENTER);
    	
    	taFront.setText("Colonization 시뮬레이션 속도를 벤치마킹할 수 있는 도구입니다.\n쓰레드와 사이클(시간) 수를 지정하신 후 버튼을 클릭해 시작하실 수 있습니다.");
    	
    	pnCenter.setLayout(new BorderLayout());
    	
    	pnArena = new JPanel();
    	pnCenter.add(new JScrollPane(pnArena, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    	
    	pnCard2.add(new JPanel(), BorderLayout.NORTH);
    	pnCard2.add(new JPanel(), BorderLayout.SOUTH);
    	
    	JPanel pnCard2Center = new JPanel();
    	pnCard2Center.setLayout(new FlowLayout(FlowLayout.CENTER));
    	pnCard2.add(pnCard2Center, BorderLayout.CENTER);
    	
    	JProgressBar progLoading = new JProgressBar();
    	progLoading.setIndeterminate(true);
    	pnCard2Center.add(progLoading);
    	
    	taResult = new JTextArea();
    	taResult.setEditable(false);
    	pnCard3.add(taResult, BorderLayout.CENTER);
    	
    	JButton btn;
    	btn = new JButton("RESET");
    	pnCard3.add(btn, BorderLayout.SOUTH);
    	
    	btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				card.show(pnCard, "C1");
				taResult.setText("");
			}
		});
    	
    	pnDown.setLayout(new FlowLayout());
    	
    	Vector<String> benchCount = new Vector<String>();
    	benchCount.add("1 thread");
    	benchCount.add("4 threads");
    	benchCount.add("16 threads");
    	benchCount.add("32 threads");
    	benchCount.add("64 threads");
    	benchCount.add("128 threads");
    	benchCount.add("256 threads");
    	
    	cbxThread = new JComboBox<String>(benchCount);
    	cbxThread.setSelectedIndex(4);
    	pnDown.add(cbxThread);
    	
    	Vector<String> cycleCount = new Vector<String>();
    	cycleCount.add("1000 cycles");
    	cycleCount.add("2000 cycles");
    	cycleCount.add("4000 cycles");
    	
    	cbxCycle = new JComboBox<String>(cycleCount);
    	pnDown.add(cbxCycle);
    	
    	btnStart = new JButton("Benchmark");
    	pnDown.add(btnStart);
    	
    	btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				start(100);
			}
		});
    	
    	new Thread(new Runnable() {
			@Override
			public void run() {
				onThread();
			}
		}).start();
    }
    
    public BenchmarkManager getSelf() {
    	return this;
    }
    
    public void start(int count) {
    	startStep1();
    	
    	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				startStep2();
			}
		});
    }
    
    protected void startStep1() {
    	threadPaused = true;
    	card.show(pnCard, "C2");
    	btnStart.setEnabled(false);
    }
    
    protected void startStep2() {
    	pnArena.removeAll();
    	
    	int maximums = 200;
    	int threadCounts = 1;
    	int sel = cbxThread.getSelectedIndex();
    	if(sel == 0) threadCounts = 1;
    	if(sel == 1) threadCounts = 4;
    	if(sel == 2) threadCounts = 16;
    	if(sel == 3) threadCounts = 32;
    	if(sel == 4) threadCounts = 64;
    	if(sel == 5) threadCounts = 128;
    	if(sel == 6) threadCounts = 256;
    	
    	int maxCycle = 1000;
    	sel = cbxCycle.getSelectedIndex();
    	if(sel == 0) maxCycle = 1000;
    	if(sel == 1) maxCycle = 2000;
    	if(sel == 2) maxCycle = 4000;
    	
    	cbxCycle.setEnabled(false);
    	cbxThread.setEnabled(false);
    	
    	for(JPanel p : pns) {
    		p.removeAll();
    	}
    	pns.clear();
    	bars.clear();
    	
    	for(BenchmarkThread t : list) {
    		t.dispose();
    	}
    	list.clear();
    	
        for(int idx=0; idx<maximums; idx++) {
        	JPanel pnOne = new JPanel();
    		pnOne.setLayout(new BorderLayout());
    		
    		pns.add(pnOne);
    		
        	if(idx < threadCounts) {
        		BenchmarkThread newInst = new BenchmarkThread(getSelf());
        		newInst.setMaxCycle(maxCycle);
        		list.add(newInst);
        		
        		JProgressBar progOne = new JProgressBar();
        		pnOne.add(progOne);
        		
        		bars.add(progOne);
        	}
    	}
        
        pnArena.setLayout(new GridLayout(pns.size(), 1));
        for(JPanel p : pns) {
        	pnArena.add(p);
        }
        
        card.show(pnCard, "C1");
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				startStep3();
			}
		}).start();
    }
    
    protected void startStep3() {
    	threadPaused = false;
    	for(BenchmarkThread t : list) {
        	t.startBench();
        }
    }
    
    protected void onThread() {
    	while(threadSwitch) {
    		if(! threadPaused) {
    			int completed = 0;
    			for(int idx=0; idx<list.size(); idx++) {
    				BenchmarkThread thr = list.get(idx);
    				JProgressBar progOne = bars.get(idx);
    				
    				int max = thr.getMaxCycle();
    				int cur = thr.getCycle();
    				
    				progOne.setMaximum(max);
    				progOne.setValue(cur);
    				
    				if(cur >= max) completed++;
    			}
    			
    			if(completed >= list.size()) {
    				threadPaused = true;
    				onEndCalled();
    			}
    		}
    		try { Thread.sleep(100L); } catch(InterruptedException ex) { break; }
    	}
    }
    
    public void onEndCalled() {
    	threadPaused = true;
    	card.show(pnCard, "C2");
    	
    	int counts = list.size();
    	List<BigDecimal> results = new ArrayList<BigDecimal>();
    	
    	try { Thread.sleep(1000L); } catch(InterruptedException ex) {  }
        pnArena.removeAll();
    	
    	for(JPanel p : pns) {
    		p.removeAll();
    	}
    	pns.clear();
    	bars.clear();
    	
    	for(BenchmarkThread t : list) {
    		results.add(new BigDecimal(String.valueOf(t.result())));
    		t.dispose();
    	}
    	list.clear();
    	
    	StringBuilder res = new StringBuilder("");
    	res = res.append("\n").append("정착지 수 : ").append(String.valueOf(counts));
    	
    	BigDecimal sum = BigDecimal.ZERO;
    	for(BigDecimal n : results) {
    		sum = sum.add(n);
    	}
    	sum.setScale(50, RoundingMode.HALF_UP);
    	BigDecimal ave = sum.divide(new BigDecimal(String.valueOf(counts)));
    	res = res.append("\n").append("소요시간 합산 : ").append(String.valueOf(sum)).append(" (낮을 수록 우수)");
    	res = res.append("\n").append("평균 소요시간 : ").append(String.valueOf(ave)).append(" (낮을 수록 우수)");
    	
    	taResult.setText(res.toString().trim());
    	card.show(pnCard, "C3");
    	
    	btnStart.setEnabled(true);
    	cbxCycle.setEnabled(true);
    	cbxThread.setEnabled(true);
    }
    
    public void open() {
    	dialog.setVisible(true);
    }
    
    public void dispose() {
    	threadSwitch = false;
    	for(JPanel p : pns) {
    		p.removeAll();
    	}
    	pns.clear();
    	for(BenchmarkThread t : list) {
    		t.dispose();
    	}
    	list.clear();
    	bars.clear();
    }
}
 