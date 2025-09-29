package org.duckdns.hjow.samples.colonyman.benchmark;
import org.duckdns.hjow.commons.core.Disposeable;
import org.duckdns.hjow.samples.colonyman.ColonyManager;
import org.duckdns.hjow.samples.colonyman.elements.Colony;

public class BenchmarkThread extends ColonyManager implements Disposeable {
	private static final long serialVersionUID = 7738631515874119141L;
	protected BenchmarkManager man;
	protected int repeats = 1000;
	protected int counts  = 0;
	protected long start = 0L, end = 0L;

	public BenchmarkThread(BenchmarkManager man) {
		super();
		this.man = man;
		
		newColony();
		selectedColony = 0;
		
		Colony col = getColony();
		col.newCity();
		col.newCity();
		
		threadPaused = true;
		cycleGap     = 30;
	}
    
	@Override
	public void dispose() {
		threadSwitch = false;
		man = null;
	}
	
	public void startBench() {
		threadPaused = true;
		threadSwitch = true;
		start  = System.currentTimeMillis();
		end    = 0;
		cycle  = 0;
		counts = 0;
		assureMainThreadRunning();
		threadPaused = false;
	}
	
	public boolean isStarted() {
		return (! threadPaused);
	}
	
	/** 메인 쓰레드 동작 */
    protected boolean onMainThread() {
    	if(! threadSwitch) return false;
    	if(! threadPaused) { oneCycle(); counts++; }
    	
    	if(counts >= repeats) {
    		threadSwitch = false;
    		end = System.currentTimeMillis();
    		return false;
    	}
    	
    	try { Thread.sleep(cycleGap); } catch(InterruptedException e) { threadSwitch = false; return false; }
        return true;
    }
    
    public int getCycle() {
    	return cycle;
    }
    
    public int getMaxCycle() {
    	return repeats;
    }
    
    public void setMaxCycle(int c) {
    	repeats = c;
    }
    
    public long result() {
    	return end - start;
    }
}
