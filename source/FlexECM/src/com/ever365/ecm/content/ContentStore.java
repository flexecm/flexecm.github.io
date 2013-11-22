package com.ever365.ecm.content;

import java.io.InputStream;

/**
 * Provides low-level retrieval of content
 * 
 * @author Liu Han
 */
public interface ContentStore
{
    /**
     * Calculates the total size of <b>stored content</b>, excluding any other data in the underlying
     * storage.
     * <p/>
     * <b>NOTE:</b> Calculating this value can be time-consuming - use sparingly.
     * <p/>
     * <b>NOTE:</b> For efficiency, some implementations may provide a guess.  If not, this call could
     * take a long time.
     * 
     * @return
     *      Returns the total, possibly approximate size (in bytes) of the binary data stored or <tt>-1</tt>
     *      if no size data is available.
     */
    public long getSpaceUsed();
    
    /**
     * Calcualates the remaing <i>free</i> space in the underlying store.
     * <p>
     * <b>NOTE:</b> For efficiency, some implementations may provide a guess.
     * <p>
     * Implementations should focus on calculating a size value quickly, rather than accurately.
     * 
     * @return
     *      Returns the total, possibly approximate, free space (in bytes) available to the store
     *      or <tt>-1</tt> if no size data is available.
     */
    public long getSpaceFree();
    
    /**
     * Calculates the total storage space of the underlying store.
     * <p>
     * <b>NOTE:</b> For efficiency, some implementations may provide a guess.
     * <p>
     * Implementations should focus on calculating a size value quickly, rather than accurately.
     * 
     * @return
     *      Returns the total, possibly approximate, size (in bytes) of the underlying store
     *      or <tt>-1</tt> if no size data is available.
     */
    public long getSpaceTotal();
    
    /**
     * Get the location where the store is rooted.  The format of the returned value will depend on the
     * specific implementation of the store.
     * 
     * @return          Returns the store's root location or <b>.</b> if no information is available
     */
    public String getRootLocation();
    
    /**
     * Check for the existence of content in the store.
     * <p>
     * The implementation of this may be more efficient than first getting a
     * reader to {@link ContentReader#exists() check for existence}, although
     * that check should also be performed.
     * 
     * @param contentUrl
     *      the path to the content
     * @return
     *      Returns true if the content exists, otherwise false if the content doesn't
     *      exist or <b>if the URL is not applicable to this store</b>.
     * @throws UnsupportedContentUrlException
     *      if the content URL supplied is not supported by the store
     * @throws ContentIOException
     *      if an IO error occurs
     * 
     * @see ContentReader#exists()
     */
    public boolean exists(String contentUrl);
    
    /**
     * Deletes the content at the given URL.
     * <p>
     * A delete cannot be forced since it is much better to have the
     * file remain longer than desired rather than deleted prematurely.
     * 
     * @param contentUrl
     *      the URL of the content to delete
     * @return 
     *      Returns <tt>true</tt> if the content was deleted (either by this or another operation),
     *      otherwise false.  If the content no longer exists, then <tt>true</tt> is returned.
     * @throws UnsupportedOperationException
     *      if the store is unable to perform the action
     * @throws UnsupportedContentUrlException
     *      if the content URL supplied is not supported by the store
     * @throws ContentIOException if an error occurs
     *      if an IO error occurs
     */
    public boolean deleteContentData(String contentUrl);
    
    public boolean isAvailable();
    
    /**
     * get content data throught the url 
     * The url  
     * 
     * @param contentUrl
     * @return
     */
    public ContentData getContentData(String contentUrl);

    /**
     * check weather the store match the store url;
     * @param storeUrl
     * @return
     */
    public void setContentUrl(String storeUrl);
    
    public String getStoreName();
    
    /**
     * The store location with access methods and location 
     * for local FS:   FS://D:/store,  FS://../file_data
     * @return
     */
    public String getStoreUrl();
    
    /**
     * @param inputStream
     * @param contentType
     * @param size
     * @return the ContentId with which the content could be gotten
     */
    public String putContent(InputStream inputStream, long size);
    
    /**
     * 
     * @param inputStream
     * @param contentType
     * @param size
     * @return 
     */
    public String putContent(String contentId, InputStream inputStream, long offset, long length);
    
}
