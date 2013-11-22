package com.ever365.ecm.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.util.FileCopyUtils;

import com.ever365.ecm.authority.AuthenticationUtil;
import com.ever365.utils.UUID;

/**
 * @author Liu Han
 */
public class FSContentStore implements ContentStore {

	public static final String PROTOCOL = "fs://";
	
	private String name;
	private String rootDir;
	private File rootDirectory = null;
	
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void init() {
		rootDirectory = new File(rootDir); 
		if (!rootDirectory.exists())
        {
            if (!rootDirectory.mkdirs())
            {
                throw new RuntimeException();
            }
        }		
	}
	
	@Override
	public String putContent(InputStream inputStream, long size) {
		
		String storeUrl = createNewFileStoreUrl();
		
		File newFile = new File(rootDirectory, storeUrl);
		
		File dir = newFile.getParentFile();
		if (!dir.exists())
		{
		    makeDirectory(dir);
		}
        try {
			boolean created = newFile.createNewFile();
			if (!created) {
				throw new IOException("File can not be created");
			}
			FileCopyUtils.copy(inputStream, new FileOutputStream(newFile));
			return storeUrl;
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}

	 public static String createNewFileStoreUrl() {
		Calendar calendar = new GregorianCalendar();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;  // 0-based
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		// create the URL
		StringBuilder sb = new StringBuilder(20);
		sb.append(AuthenticationUtil.getTenant()).append('/')
		  .append(year).append('/')
		  .append(month).append('/')
		  .append(day).append('/')
		  .append(hour).append('/')
		  .append(minute).append('/')
		  .append(UUID.generate()).append(".bin");
		String newContentUrl = sb.toString();
		// done
		return newContentUrl;
	 }

  /**
     * Synchronized and retrying directory creation.  Repeated attempts will be made to create the
     * directory, subject to a limit on the number of retries.
     * 
     * @param dir               the directory to create
     * @throws IOException      if an IO error occurs
     */
    private synchronized void makeDirectory(File dir) 
    {
        /*
         * Once in this method, the only contention will be from other file stores or processes.
         * This is OK as we have retrying to sort it out.
         */
        if (dir.exists())
        {
            // Beaten to it during synchronization
            return;
        }
        // 20 attempts with 20 ms wait each time
        for (int i = 0; i < 20; i++)
        {
            boolean created = dir.mkdirs();
            if (created)
            {
                // Successfully created
                return;
            }
            // Wait
            try { this.wait(20L); } catch (InterruptedException e) {}
            // Did it get created in the meantime
            if (dir.exists())
            {
                // Beaten to it while asleep
                return;
            }
        }
        // It still didn't succeed
    }
	
	@Override
	public long getSpaceUsed() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public long getSpaceFree() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public long getSpaceTotal() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getRootLocation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean exists(String contentUrl) {
		return false;
	}
	
	@Override
	public boolean deleteContentData(String contentUrl) {
		return false;
	}
	
	@Override
	public boolean isAvailable() {
		return true;
	}
	
	@Override
	public ContentData getContentData(String contentUrl) {
		return null;
	}
	
	@Override
	public String getStoreName() {
		return name;
	}
	
	@Override
	public String getStoreUrl() {
		return PROTOCOL + rootDir;
	}

	@Override
	public String putContent(String contentId, InputStream inputStream,
			long offset, long length) {
		return null;
	}

	@Override
	public void setContentUrl(String storeUrl) {
		this.rootDir =  storeUrl.substring(PROTOCOL.length());
		init();
	}

	    
}
