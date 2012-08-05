/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.client;

/**
 * A class to hold user agent specific properties. For example, in streaming
 * endpoints, a certain number of bytes need to be written before the
 * streaming connection can be used and this value is specific to user agents.
 * Similarly, the number of simultaneous connections a session can have is user
 * agent specific.
 */
public class UserAgentSettings
{
    /**
     * The prefixes of the version token used by various browsers.
     */
    public static final String USER_AGENT_CHROME = "Chrome";
    public static final String USER_AGENT_FIREFOX = "Firefox";
    public static final String USER_AGENT_FIREFOX_3 = "Firefox/3";
    public static final String USER_AGENT_MSIE = "MSIE";
    public static final String USER_AGENT_MSIE_8 = "MSIE 8";
    public static final String USER_AGENT_OPERA = "Opera";
    public static final String USER_AGENT_OPERA_8 = "Opera 8";
    // Opera 10 apparently ships as User Agent Opera/9.8.
    public static final String USER_AGENT_OPERA_10 = "Opera/9.8"; 
    public static final String USER_AGENT_SAFARI = "Safari";

    /**
     * Bytes needed to kickstart the streaming connections for IE.
     */
    public static final int KICKSTART_BYTES_MSIE = 2048;
    /**
     * Bytes needed to kickstart the streaming connections for SAFARI.
     */
    public static final int KICKSTART_BYTES_SAFARI = 512;

    /**
     * The default number of persistent connections per session for various browsers.
     */
    public static final int MAX_PERSISTENT_CONNECTIONS_DEFAULT =  1;
    private static final int MAX_PERSISTENT_CONNECTIONS_CHROME = 5;
    private static final int MAX_PERSISTENT_CONNECTIONS_FIREFOX_3 = 5;
    private static final int MAX_PERSISTENT_CONNECTIONS_MSIE_8 = 5;
    private static final int MAX_PERSISTENT_CONNECTIONS_OPERA = 3;
    private static final int MAX_PERSISTENT_CONNECTIONS_OPERA_8 = 7;
    private static final int MAX_PERSISTENT_CONNECTIONS_OPERA_10 = 7;
    private static final int MAX_PERSISTENT_CONNECTIONS_SAFARI = 3;

    private String matchOn;
    private int kickstartBytes;
    private int maxPersistentConnectionsPerSession = MAX_PERSISTENT_CONNECTIONS_DEFAULT;

    /**
     *  Static method to retrieve pre-initialized user agents which are as follows:
     *
     *  In Chrome 0, 1, 2, the limit is 6:
     *      match-on="Chrome" max-persistent-connections-per-session="5"
     *
     *  In Firefox 1, 2, the limit is 2:
     *      match-on="Firefox" max-persistent-connections-per-session="1"
     *
     *  In Firefox 3, the limit is 6:
     *      match-on="Firefox/3" max-persistent-connections-per-session="5"
     *
     *  In MSIE 5, 6, 7, the limit is 2 with kickstart bytes of 2K:
     *      match-on="MSIE" max-persistent-connections-per-session="1" kickstart-bytes="2048"
     *
     *  In MSIE 8, the limit is 6 with kickstart bytes of 2K:
     *      match-on="MSIE 8" max-persistent-connections-per-session="5" kickstart-bytes="2048"
     *
     *  In Opera 7, 9, the limit is 4:
     *      match-on="Opera" max-persistent-connections-per-session="3"
     *
     *  In Opera 8, the limit is 8:
     *      match-on="Opera 8" max-persistent-connections-per-session="7"
     *
     *  In Opera 10, the limit is 8.
     *      match-on="Opera 10" max-persistent-connections-per-session="7"
     *
     *  In Safari 3, 4, the limit is 4.
     *      match-on="Safari" max-persistent-connections-per-session="3"
     *
     * @param matchOn String to use match the agent.
     */
    public static UserAgentSettings getAgent(String matchOn)
    {
        UserAgentSettings userAgent = new UserAgentSettings();
        userAgent.setMatchOn(matchOn);
        if (USER_AGENT_CHROME.equals(matchOn))
        {
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_CHROME);
        }
        else if (USER_AGENT_FIREFOX.equals(matchOn))
        {
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_DEFAULT);
        }
        else if (USER_AGENT_FIREFOX_3.equals(matchOn))
        {
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_FIREFOX_3);
        }
        else if (USER_AGENT_MSIE.equals(matchOn))
        {
            userAgent.setKickstartBytes(KICKSTART_BYTES_MSIE);
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_DEFAULT);
        }
        else if (USER_AGENT_MSIE_8.equals(matchOn))
        {
            userAgent.setKickstartBytes(KICKSTART_BYTES_MSIE);
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_MSIE_8);
        }
        else if (USER_AGENT_OPERA.equals(matchOn))
        {
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_OPERA);
        }
        else if (USER_AGENT_OPERA_8.equals(matchOn))
        {
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_OPERA_8);
        }
        else if (USER_AGENT_OPERA_10.equals(matchOn))
        {
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_OPERA_10);
        }
        else if (USER_AGENT_SAFARI.equals(matchOn))
        {
            userAgent.setKickstartBytes(KICKSTART_BYTES_SAFARI);
            userAgent.setMaxPersistentConnectionsPerSession(MAX_PERSISTENT_CONNECTIONS_SAFARI);
        }
        return userAgent;
    }

    /**
     * Returns the String to use to match the agent.
     *
     * @return The String to use to match the agent.
     */
    public String getMatchOn()
    {
        return matchOn;
    }

    /**
     * Sets the String to use to match the agent.
     *
     * @param matchOn The String to use to match the agent.
     */
    public void setMatchOn(String matchOn)
    {
        this.matchOn = matchOn;
    }

    /**
     * Returns the number of bytes needed to kickstart the streaming connections
     * for the user agent.
     *
     * @return The number of bytes needed to kickstart the streaming connections
     * for the user agent.
     */
    public int getKickstartBytes()
    {
        return kickstartBytes;
    }

    /**
     * Sets the number of bytes needed to kickstart the streaming connections
     * for the user agent.
     *
     * @param kickstartBytes The number of bytes needed to kickstart the streaming
     * connections for the user agent.
     */
    public void setKickstartBytes(int kickstartBytes)
    {
        if (kickstartBytes < 0)
            kickstartBytes = 0;
        this.kickstartBytes = kickstartBytes;
    }

    /**
     * @deprecated Use {@link UserAgentSettings#getMaxPersistentConnectionsPerSession()} instead.
     */
    public int getMaxStreamingConnectionsPerSession()
    {
        return getMaxPersistentConnectionsPerSession();
    }

    /**
     * @deprecated Use {@link UserAgentSettings#setMaxPersistentConnectionsPerSession(int)} instead.
     */
    public void setMaxStreamingConnectionsPerSession(int maxStreamingConnectionsPerSession)
    {
        setMaxPersistentConnectionsPerSession(maxStreamingConnectionsPerSession);
    }

    /**
     * Returns the number of simultaneous streaming connections per session
     * the user agent supports.
     *
     * @return The number of streaming connections per session the user agent supports.
     */
    public int getMaxPersistentConnectionsPerSession()
    {
        return maxPersistentConnectionsPerSession;
    }

    /**
     * Sets the number of simultaneous streaming connections per session
     * the user agent supports.
     *
     * @param maxStreamingConnectionsPerSession The number of simultaneous
     * streaming connections per session the user agent supports.
     */
    public void setMaxPersistentConnectionsPerSession(int maxStreamingConnectionsPerSession)
    {
        this.maxPersistentConnectionsPerSession = maxStreamingConnectionsPerSession;
    }

}
