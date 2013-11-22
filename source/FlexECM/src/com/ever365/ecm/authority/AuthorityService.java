package com.ever365.ecm.authority;

import java.util.Set;

public interface AuthorityService {
    
    /**
     * Check of the current user has admin authority.
     * 
     * There is no contract for who should have this authority, only that it can
     * be tested here. It could be determined by group membership, role,
     * authentication mechanism, ...
     * 
     * @return true if the currently authenticated user has the admin authority
     */
    public boolean hasAdminAuthority();
    
    /**
     * Does the given authority have admin authority.
     * @param authorityName The name of the authority.
     * @return Whether the authority is an 'administrator'.
     */
    public boolean isAdminAuthority(String authorityName);

    /**
     * Check of the current user has guest authority.
     * 
     * There is no contract for who should have this authority, only that it can
     * be tested here. It could be determined by group membership, role,
     * authentication mechanism, ...
     * 
     * @return true if the currently authenticated user has the guest authority
     */
    public boolean hasGuestAuthority();
    
    /**
     * Does the given authority have guest authority.
     *  
     * @param authorityName The name of the authority.
     * @return Whether the authority is a 'guest'.
     */
    public boolean isGuestAuthority(String authorityName);

    /**
     * Get the authorities for the loginedId (authority)
     * @return authorities for the current user
     */
    public Set<String> getAuthorities(String loginId);
    
    /**
     * Get all root authorities by type. Root authorities are ones that were
     * created without an authority as the parent authority;
     * 
     * @param type -
     *            the type of the authority
     * @return all root authorities by type.
     */
    public Set<String> getAllRootAuthorities();

    /**
     * Create an authority.
     * 
     * @param type -
     *            the type of the authority
     * @param shortName -
     *            the short name of the authority to create
     *            this will also be set as the default display name for the authority 
     * 
     * @return the name of the authority (this will be the prefix, if any
     *         associated with the type appended with the short name)
     */
    public String createAuthority(String type, String shortName);

    /**
     * Set an authority to include another authority. For example, adding a
     * group to a group or adding a user to a group.
     * 
     * @param parentName -
     *            the full name string identifier for the parent.
     * @param childName -
     *            the string identifier for the child.
     */
    public void addAuthority(String parentName, String childName);


    /**
     * Remove an authority as a member of another authority. The child authority
     * will still exist. If the child authority was not created as a root
     * authority and you remove its creation link, it will be moved to a root
     * authority. If you want rid of it, use delete.
     * 
     * @param parentName -
     *            the string identifier for the parent.
     * @param childName -
     *            the string identifier for the child.
     */
    public void removeAuthority(String parentName, String childName);
   
    /**
     * Delete an authority and all its relationships, optionally recursively deleting child authorities of the same
     * type.
     * 
     * @param name
     *            the authority long name
     * @param cascade
     *            should the delete be cascaded to child authorities of the same type?
     */
    public void deleteAuthority(String name, boolean cascade);

    /**
     * Get all the authorities that are contained by the given authority.
     * 
     * For a group you could get all the authorities it contains, just the users
     * it contains or just the other groups it includes.
     * 
     * @param type -
     *            if not null, limit to the type of authority specified
     * @param name -
     *            the name of the containing authority
     * @param immediate -
     *            if true, limit the depth to just immediate child, if false
     *            find authorities at any depth
     * @return
     */
    public Set<String> getContainedAuthorities(String name);

  /**
     * Check if an authority exists.
     * 
     * @param name (the long name). 
     * @return true, the authority exists.
     */
    public boolean authorityExists(String name);
}

