package bufmgr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chainexception.ChainException;
import global.Convert;
import global.GlobalConst;
import global.Minibase;
import global.Page;
import global.PageId;

public class BufFrmDescriptor implements Comparable<BufFrmDescriptor> {
	
//-----------------------------------------------------------------------------------------------------------
	// Class declarations and access methods
//---------------------------------------------------------------------------------------------------------------	

	private static MyHashTable pageId_frameId_lookup = new MyHashTable();
	
	public static void resetPageId_frameId_lookup()
	{
		pageId_frameId_lookup = new MyHashTable();
	}
	
	
	public static Integer getFrameIDForPageID(Integer pid) throws ChainException
	{
		if(!pageId_frameId_lookup.containsKey(pid))
		{
			throw new HashEntryNotFoundException(new Exception(),"ERROR MSG : Hash entry not found exception.");
		}
		return pageId_frameId_lookup.get(pid);
	}
	
	
	public static void removeFrameIDForPageID(Integer pid)
	{
	//	System.out.println("Request to remove page id " + pid);
		pageId_frameId_lookup.remove(pid);
		return;
	}	
	
		
//---------------------------------------------------------------------------------------------------------------	
	// Object declarations and access methods
//---------------------------------------------------------------------------------------------------------------	
	
	// TODO: Change refer to system time
	private List<Long> referenceTimes;
	private int pin_count;
	private byte[] frame_data;
	private Integer fid;
	private PageId page_number;
	private boolean dirty;
	
	
	
// ----------------------------------------------------------------------
	// Getters and setters
// ---------------------------------------------------------------------

	public int getPin_count() {
		return pin_count;
	}


	public void setPin_count(int pin_count) {
		this.pin_count = pin_count;
	}


	public byte[] getFrame_data() {
		return frame_data;
	}


	public void setFrame_data(byte[] frame_data) {
		this.frame_data = frame_data;
	}


	public Integer getFid() {
		return fid;
	}


	public void setFid(Integer fid) {
		this.fid = fid;
	}


	public PageId getPage_number() {
		return page_number;
	}


	public void setPage_number(PageId page_number) {
		this.page_number = new PageId();
		this.page_number.pid = page_number.pid;
	}


	public boolean isDirty() {
		return dirty;
	}


	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public List<Long> getReferenceTimes() {
		return referenceTimes;
	}


	public void setReferenceTimes(List<Long> referenceTimes) {
		this.referenceTimes = referenceTimes;
	}


//--------------------------------------------------------------------------------	
	// Object defs
//-------------------------------------------------------------------------------
	public BufFrmDescriptor(int fid) {
		super();
		this.pin_count = 0;
		this.frame_data = new byte[global.GlobalConst.PAGE_SIZE];
		this.fid = fid;
		this.dirty = false;
		this.referenceTimes = new ArrayList<Long>();
		this.page_number = null;
	}

	public void resetFrame()
	{
		this.pin_count = 0;
		this.frame_data = null;
		this.dirty = false;
		this.referenceTimes.clear();
		this.page_number = null;
		this.frame_data = new byte[global.GlobalConst.PAGE_SIZE];
	}
	
	public void insertIntoFrame(Page page, PageId pageId) throws ChainException
	{
		resetFrame();
		page.setpage(frame_data);
		try {
			Minibase.DiskManager.read_page(pageId, page);
		} catch (Exception e) {
			throw new ChainException(e, "ERROR MSG: couldn't read from disk. ");
		}
		// Insert into lookup tables	
		pageId_frameId_lookup.put(pageId.pid, this.fid);
		
		// Update local vars
		this.setPage_number(pageId);
		
		// Update the data frame
		this.setFrame_data(page.getData());
		
		// Update the crf time
		this.referenceTimes.add(BufMgr.getCurrentTime());
		this.dirty = false;
		this.pin_count = 1;
	}
	
	@Override
	public String toString() {
		if(page_number == null)
		{
			try {
				return "BufFrmDescriptor [crf = " +  crf(BufMgr.getCurrentTime()) + " references" + referenceTimes +  ", pin_count=" + pin_count +  ", fid=" + fid  + " dirty=" + dirty
						+ "current time " + BufMgr.getCurrentTime() + " data "  + Convert.getIntValue (0, getFrame_data()) +  "]";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}	
		} else
			try {
				return "BufFrmDescriptor [crf = " +  crf(BufMgr.getCurrentTime()) + " references" + referenceTimes +  ", pin_count=" + pin_count +  ", fid=" + fid + ", page_number=" + page_number.pid + ", dirty=" + dirty
						+ "current time " + BufMgr.getCurrentTime() + " data "  + Convert.getIntValue (0, getFrame_data()) +  "]";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
	}


	public double crf(long currentTime)
	{
		double crfval =0.0;
		for(int a =0 ; a < referenceTimes.size(); a++)
		{
			crfval = crfval + (double)(1.0d/ ( X(referenceTimes.get(a), currentTime) + 1));
		}
		return crfval;
	}
	
	private long X(long referenceTime, long currentTime)
	{
		return currentTime - referenceTime;
	}

	@Override
	public int compareTo(BufFrmDescriptor o) {
		Double crfA = new Double(this.crf(BufMgr.getCurrentTime()));
		Double crfB = new Double(o.crf(BufMgr.getCurrentTime()));
		return crfA.compareTo(crfB);
	}
	
	
}
