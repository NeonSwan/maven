package org.apache.maven.execution;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Profile;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.automirror.MirrorRoutingTable;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.sonatype.aether.RepositoryCache;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.transfer.TransferListener;
import org.sonatype.aether.util.DefaultRepositoryCache;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author Jason van Zyl
 */
public class DefaultMavenExecutionRequest
    implements MavenExecutionRequest
{

    private RepositoryCache repositoryCache = new DefaultRepositoryCache();

    private WorkspaceReader workspaceReader;

    private ArtifactRepository localRepository;

    private File localRepositoryPath;

    private boolean offline = false;

    private boolean interactiveMode = true;

    private boolean cacheTransferError;

    private boolean cacheNotFound;

    private List<Proxy> proxies;

    private List<Server> servers;

    private List<Mirror> mirrors;

    private List<Profile> profiles;

    private List<String> pluginGroups;

    private boolean isProjectPresent = true;

    // ----------------------------------------------------------------------------
    // We need to allow per execution user and global settings as the embedder
    // might be running in a mode where its executing many threads with totally
    // different settings.
    // ----------------------------------------------------------------------------

    private File userSettingsFile;

    private File globalSettingsFile;

    private File userToolchainsFile;

    // ----------------------------------------------------------------------------
    // Request
    // ----------------------------------------------------------------------------

    private File basedir;

    private List<String> goals;

    private boolean useReactor = false;

    private boolean recursive = true;

    private File pom;

    private String reactorFailureBehavior = REACTOR_FAIL_FAST;

    private List<String> selectedProjects;

    private String resumeFrom;

    private String makeBehavior;

    private Properties systemProperties;

    private Properties userProperties;

    private Date startTime;

    private boolean showErrors = false;

    private List<String> activeProfiles;

    private List<String> inactiveProfiles;

    private TransferListener transferListener;

    private int loggingLevel = LOGGING_LEVEL_INFO;

    private String globalChecksumPolicy;

    private boolean updateSnapshots = false;

    private List<ArtifactRepository> remoteRepositories;

    private List<ArtifactRepository> pluginArtifactRepositories;

    private ExecutionListener executionListener;

    private String threadCount;

    private boolean perCoreThreadCount;

    /**
     * Suppress SNAPSHOT updates.
     * 
     * @issue MNG-2681
     */
    private boolean noSnapshotUpdates;

    public DefaultMavenExecutionRequest()
    {
    }

    public static MavenExecutionRequest copy( final MavenExecutionRequest original )
    {
        final DefaultMavenExecutionRequest copy = new DefaultMavenExecutionRequest();
        copy.setLocalRepository( original.getLocalRepository() );
        copy.setLocalRepositoryPath( original.getLocalRepositoryPath() );
        copy.setOffline( original.isOffline() );
        copy.setInteractiveMode( original.isInteractiveMode() );
        copy.setCacheNotFound( original.isCacheNotFound() );
        copy.setCacheTransferError( original.isCacheTransferError() );
        copy.setProxies( original.getProxies() );
        copy.setServers( original.getServers() );
        copy.setMirrors( original.getMirrors() );
        copy.setProfiles( original.getProfiles() );
        copy.setPluginGroups( original.getPluginGroups() );
        copy.setProjectPresent( original.isProjectPresent() );
        copy.setUserSettingsFile( original.getUserSettingsFile() );
        copy.setGlobalSettingsFile( original.getGlobalSettingsFile() );
        copy.setUserToolchainsFile( original.getUserToolchainsFile() );
        copy.setBaseDirectory( ( original.getBaseDirectory() != null ) ? new File( original.getBaseDirectory() ) : null );
        copy.setGoals( original.getGoals() );
        copy.setRecursive( original.isRecursive() );
        copy.setPom( original.getPom() );
        copy.setSystemProperties( original.getSystemProperties() );
        copy.setUserProperties( original.getUserProperties() );
        copy.setShowErrors( original.isShowErrors() );
        copy.setActiveProfiles( original.getActiveProfiles() );
        copy.setInactiveProfiles( original.getInactiveProfiles() );
        copy.setTransferListener( original.getTransferListener() );
        copy.setLoggingLevel( original.getLoggingLevel() );
        copy.setGlobalChecksumPolicy( original.getGlobalChecksumPolicy() );
        copy.setUpdateSnapshots( original.isUpdateSnapshots() );
        copy.setRemoteRepositories( original.getRemoteRepositories() );
        copy.setPluginArtifactRepositories( original.getPluginArtifactRepositories() );
        copy.setRepositoryCache( original.getRepositoryCache() );
        copy.setWorkspaceReader( original.getWorkspaceReader() );
        copy.setNoSnapshotUpdates( original.isNoSnapshotUpdates() );
        copy.setExecutionListener( original.getExecutionListener() );
        copy.setMirrorRoutingTable( original.getMirrorRoutingTable() );
        return copy;
    }

    public String getBaseDirectory()
    {
        if ( basedir == null )
        {
            return null;
        }

        return basedir.getAbsolutePath();
    }

    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    public File getLocalRepositoryPath()
    {
        return localRepositoryPath;
    }

    public List<String> getGoals()
    {
        if ( goals == null )
        {
            goals = new ArrayList<String>();
        }
        return goals;
    }

    public Properties getSystemProperties()
    {
        if ( systemProperties == null )
        {
            systemProperties = new Properties();
        }

        return systemProperties;
    }

    public Properties getUserProperties()
    {
        if ( userProperties == null )
        {
            userProperties = new Properties();
        }

        return userProperties;
    }

    public File getPom()
    {
        return pom;
    }

    public String getReactorFailureBehavior()
    {
        return reactorFailureBehavior;
    }

    public List<String> getSelectedProjects()
    {
        if ( selectedProjects == null )
        {
            selectedProjects = new ArrayList<String>();
        }

        return selectedProjects;
    }

    public String getResumeFrom()
    {
        return resumeFrom;
    }

    public String getMakeBehavior()
    {
        return makeBehavior;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public boolean isShowErrors()
    {
        return showErrors;
    }

    public boolean isInteractiveMode()
    {
        return interactiveMode;
    }

    public MavenExecutionRequest setActiveProfiles( final List<String> activeProfiles )
    {
        if ( activeProfiles != null )
        {
            this.activeProfiles = new ArrayList<String>( activeProfiles );
        }
        else
        {
            this.activeProfiles = null;
        }

        return this;
    }

    public MavenExecutionRequest setInactiveProfiles( final List<String> inactiveProfiles )
    {
        if ( inactiveProfiles != null )
        {
            this.inactiveProfiles = new ArrayList<String>( inactiveProfiles );
        }
        else
        {
            this.inactiveProfiles = null;
        }

        return this;
    }

    public MavenExecutionRequest setRemoteRepositories( final List<ArtifactRepository> remoteRepositories )
    {
        if ( remoteRepositories != null )
        {
            this.remoteRepositories = new ArrayList<ArtifactRepository>( remoteRepositories );
        }
        else
        {
            this.remoteRepositories = null;
        }

        return this;
    }

    public MavenExecutionRequest setPluginArtifactRepositories( final List<ArtifactRepository> pluginArtifactRepositories )
    {
        if ( pluginArtifactRepositories != null )
        {
            this.pluginArtifactRepositories = new ArrayList<ArtifactRepository>( pluginArtifactRepositories );
        }
        else
        {
            this.pluginArtifactRepositories = null;
        }

        return this;
    }

    public void setProjectBuildingConfiguration( final ProjectBuildingRequest projectBuildingConfiguration )
    {
        projectBuildingRequest = projectBuildingConfiguration;
    }

    public List<String> getActiveProfiles()
    {
        if ( activeProfiles == null )
        {
            activeProfiles = new ArrayList<String>();
        }
        return activeProfiles;
    }

    public List<String> getInactiveProfiles()
    {
        if ( inactiveProfiles == null )
        {
            inactiveProfiles = new ArrayList<String>();
        }
        return inactiveProfiles;
    }

    public TransferListener getTransferListener()
    {
        return transferListener;
    }

    public int getLoggingLevel()
    {
        return loggingLevel;
    }

    public boolean isOffline()
    {
        return offline;
    }

    public boolean isUpdateSnapshots()
    {
        return updateSnapshots;
    }

    public boolean isNoSnapshotUpdates()
    {
        return noSnapshotUpdates;
    }

    public String getGlobalChecksumPolicy()
    {
        return globalChecksumPolicy;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public MavenExecutionRequest setBaseDirectory( final File basedir )
    {
        this.basedir = basedir;

        return this;
    }

    public MavenExecutionRequest setStartTime( final Date startTime )
    {
        this.startTime = startTime;

        return this;
    }

    public MavenExecutionRequest setShowErrors( final boolean showErrors )
    {
        this.showErrors = showErrors;

        return this;
    }

    public MavenExecutionRequest setGoals( final List<String> goals )
    {
        if ( goals != null )
        {
            this.goals = new ArrayList<String>( goals );
        }
        else
        {
            this.goals = null;
        }

        return this;
    }

    public MavenExecutionRequest setLocalRepository( final ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;

        if ( localRepository != null )
        {
            setLocalRepositoryPath( new File( localRepository.getBasedir() ).getAbsoluteFile() );
        }

        return this;
    }

    public MavenExecutionRequest setLocalRepositoryPath( final File localRepository )
    {
        localRepositoryPath = localRepository;

        return this;
    }

    public MavenExecutionRequest setLocalRepositoryPath( final String localRepository )
    {
        localRepositoryPath = ( localRepository != null ) ? new File( localRepository ) : null;

        return this;
    }

    public MavenExecutionRequest setSystemProperties( final Properties properties )
    {
        if ( properties != null )
        {
            systemProperties = new Properties();
            systemProperties.putAll( properties );
        }
        else
        {
            systemProperties = null;
        }

        return this;
    }

    public MavenExecutionRequest setUserProperties( final Properties userProperties )
    {
        if ( userProperties != null )
        {
            this.userProperties = new Properties();
            this.userProperties.putAll( userProperties );
        }
        else
        {
            this.userProperties = null;
        }

        return this;
    }

    public MavenExecutionRequest setReactorFailureBehavior( final String failureBehavior )
    {
        reactorFailureBehavior = failureBehavior;

        return this;
    }

    public MavenExecutionRequest setSelectedProjects( final List<String> selectedProjects )
    {
        if ( selectedProjects != null )
        {
            this.selectedProjects = new ArrayList<String>( selectedProjects );
        }
        else
        {
            this.selectedProjects = null;
        }

        return this;
    }

    public MavenExecutionRequest setResumeFrom( final String project )
    {
        resumeFrom = project;

        return this;
    }

    public MavenExecutionRequest setMakeBehavior( final String makeBehavior )
    {
        this.makeBehavior = makeBehavior;

        return this;
    }

    public MavenExecutionRequest addActiveProfile( final String profile )
    {
        if ( !getActiveProfiles().contains( profile ) )
        {
            getActiveProfiles().add( profile );
        }

        return this;
    }

    public MavenExecutionRequest addInactiveProfile( final String profile )
    {
        if ( !getInactiveProfiles().contains( profile ) )
        {
            getInactiveProfiles().add( profile );
        }

        return this;
    }

    public MavenExecutionRequest addActiveProfiles( final List<String> profiles )
    {
        for ( final String profile : profiles )
        {
            addActiveProfile( profile );
        }

        return this;
    }

    public MavenExecutionRequest addInactiveProfiles( final List<String> profiles )
    {
        for ( final String profile : profiles )
        {
            addInactiveProfile( profile );
        }

        return this;
    }

    public MavenExecutionRequest setUseReactor( final boolean reactorActive )
    {
        useReactor = reactorActive;

        return this;
    }

    public boolean useReactor()
    {
        return useReactor;
    }

    /** @deprecated use {@link #setPom(File)} */
    @Deprecated
    public MavenExecutionRequest setPomFile( final String pomFilename )
    {
        if ( pomFilename != null )
        {
            pom = new File( pomFilename );
        }

        return this;
    }

    public MavenExecutionRequest setPom( final File pom )
    {
        this.pom = pom;

        return this;
    }

    public MavenExecutionRequest setInteractiveMode( final boolean interactive )
    {
        interactiveMode = interactive;

        return this;
    }

    public MavenExecutionRequest setTransferListener( final TransferListener transferListener )
    {
        this.transferListener = transferListener;

        return this;
    }

    public MavenExecutionRequest setLoggingLevel( final int loggingLevel )
    {
        this.loggingLevel = loggingLevel;

        return this;
    }

    public MavenExecutionRequest setOffline( final boolean offline )
    {
        this.offline = offline;

        return this;
    }

    public MavenExecutionRequest setUpdateSnapshots( final boolean updateSnapshots )
    {
        this.updateSnapshots = updateSnapshots;

        return this;
    }

    public MavenExecutionRequest setNoSnapshotUpdates( final boolean noSnapshotUpdates )
    {
        this.noSnapshotUpdates = noSnapshotUpdates;

        return this;
    }

    public MavenExecutionRequest setGlobalChecksumPolicy( final String globalChecksumPolicy )
    {
        this.globalChecksumPolicy = globalChecksumPolicy;

        return this;
    }

    // ----------------------------------------------------------------------------
    // Settings equivalents
    // ----------------------------------------------------------------------------

    public List<Proxy> getProxies()
    {
        if ( proxies == null )
        {
            proxies = new ArrayList<Proxy>();
        }
        return proxies;
    }

    public MavenExecutionRequest setProxies( final List<Proxy> proxies )
    {
        if ( proxies != null )
        {
            this.proxies = new ArrayList<Proxy>( proxies );
        }
        else
        {
            this.proxies = null;
        }

        return this;
    }

    public MavenExecutionRequest addProxy( final Proxy proxy )
    {
        if ( proxy == null )
        {
            throw new IllegalArgumentException( "proxy missing" );
        }

        for ( final Proxy p : getProxies() )
        {
            if ( p.getId() != null && p.getId().equals( proxy.getId() ) )
            {
                return this;
            }
        }

        getProxies().add( proxy );

        return this;
    }

    public List<Server> getServers()
    {
        if ( servers == null )
        {
            servers = new ArrayList<Server>();
        }
        return servers;
    }

    public MavenExecutionRequest setServers( final List<Server> servers )
    {
        if ( servers != null )
        {
            this.servers = new ArrayList<Server>( servers );
        }
        else
        {
            this.servers = null;
        }

        return this;
    }

    public MavenExecutionRequest addServer( final Server server )
    {
        if ( server == null )
        {
            throw new IllegalArgumentException( "server missing" );
        }

        for ( final Server p : getServers() )
        {
            if ( p.getId() != null && p.getId().equals( server.getId() ) )
            {
                return this;
            }
        }

        getServers().add( server );

        return this;
    }

    public List<Mirror> getMirrors()
    {
        if ( mirrors == null )
        {
            mirrors = new ArrayList<Mirror>();
        }
        return mirrors;
    }

    public MavenExecutionRequest setMirrors( final List<Mirror> mirrors )
    {
        if ( mirrors != null )
        {
            this.mirrors = new ArrayList<Mirror>( mirrors );
        }
        else
        {
            this.mirrors = null;
        }

        return this;
    }

    public MavenExecutionRequest addMirror( final Mirror mirror )
    {
        if ( mirror == null )
        {
            throw new IllegalArgumentException( "mirror missing" );
        }

        for ( final Mirror p : getMirrors() )
        {
            if ( p.getId() != null && p.getId().equals( mirror.getId() ) )
            {
                return this;
            }
        }

        getMirrors().add( mirror );

        return this;
    }

    public List<Profile> getProfiles()
    {
        if ( profiles == null )
        {
            profiles = new ArrayList<Profile>();
        }
        return profiles;
    }

    public MavenExecutionRequest setProfiles( final List<Profile> profiles )
    {
        if ( profiles != null )
        {
            this.profiles = new ArrayList<Profile>( profiles );
        }
        else
        {
            this.profiles = null;
        }

        return this;
    }

    public List<String> getPluginGroups()
    {
        if ( pluginGroups == null )
        {
            pluginGroups = new ArrayList<String>();
        }

        return pluginGroups;
    }

    public MavenExecutionRequest setPluginGroups( final List<String> pluginGroups )
    {
        if ( pluginGroups != null )
        {
            this.pluginGroups = new ArrayList<String>( pluginGroups );
        }
        else
        {
            this.pluginGroups = null;
        }

        return this;
    }

    public MavenExecutionRequest addPluginGroup( final String pluginGroup )
    {
        if ( !getPluginGroups().contains( pluginGroup ) )
        {
            getPluginGroups().add( pluginGroup );
        }

        return this;
    }

    public MavenExecutionRequest addPluginGroups( final List<String> pluginGroups )
    {
        for ( final String pluginGroup : pluginGroups )
        {
            addPluginGroup( pluginGroup );
        }

        return this;
    }

    public MavenExecutionRequest setRecursive( final boolean recursive )
    {
        this.recursive = recursive;

        return this;
    }

    // calculated from request attributes.
    private ProjectBuildingRequest projectBuildingRequest;

    public boolean isProjectPresent()
    {
        return isProjectPresent;
    }

    public MavenExecutionRequest setProjectPresent( final boolean projectPresent )
    {
        isProjectPresent = projectPresent;

        return this;
    }

    // Settings files

    public File getUserSettingsFile()
    {
        return userSettingsFile;
    }

    public MavenExecutionRequest setUserSettingsFile( final File userSettingsFile )
    {
        this.userSettingsFile = userSettingsFile;

        return this;
    }

    public File getGlobalSettingsFile()
    {
        return globalSettingsFile;
    }

    public MavenExecutionRequest setGlobalSettingsFile( final File globalSettingsFile )
    {
        this.globalSettingsFile = globalSettingsFile;

        return this;
    }

    public File getUserToolchainsFile()
    {
        return userToolchainsFile;
    }

    public MavenExecutionRequest setUserToolchainsFile( final File userToolchainsFile )
    {
        this.userToolchainsFile = userToolchainsFile;

        return this;
    }

    public MavenExecutionRequest addRemoteRepository( final ArtifactRepository repository )
    {
        for ( final ArtifactRepository repo : getRemoteRepositories() )
        {
            if ( repo.getId() != null && repo.getId().equals( repository.getId() ) )
            {
                return this;
            }
        }

        getRemoteRepositories().add( repository );

        return this;
    }

    public List<ArtifactRepository> getRemoteRepositories()
    {
        if ( remoteRepositories == null )
        {
            remoteRepositories = new ArrayList<ArtifactRepository>();
        }
        return remoteRepositories;
    }

    public MavenExecutionRequest addPluginArtifactRepository( final ArtifactRepository repository )
    {
        for ( final ArtifactRepository repo : getPluginArtifactRepositories() )
        {
            if ( repo.getId() != null && repo.getId().equals( repository.getId() ) )
            {
                return this;
            }
        }

        getPluginArtifactRepositories().add( repository );

        return this;
    }

    public List<ArtifactRepository> getPluginArtifactRepositories()
    {
        if ( pluginArtifactRepositories == null )
        {
            pluginArtifactRepositories = new ArrayList<ArtifactRepository>();
        }
        return pluginArtifactRepositories;
    }

    // TODO: this does not belong here.
    public ProjectBuildingRequest getProjectBuildingRequest()
    {
        if ( projectBuildingRequest == null )
        {
            projectBuildingRequest = new DefaultProjectBuildingRequest();
            projectBuildingRequest.setLocalRepository( getLocalRepository() );
            projectBuildingRequest.setSystemProperties( getSystemProperties() );
            projectBuildingRequest.setUserProperties( getUserProperties() );
            projectBuildingRequest.setRemoteRepositories( getRemoteRepositories() );
            projectBuildingRequest.setPluginArtifactRepositories( getPluginArtifactRepositories() );
            projectBuildingRequest.setActiveProfileIds( getActiveProfiles() );
            projectBuildingRequest.setInactiveProfileIds( getInactiveProfiles() );
            projectBuildingRequest.setProfiles( getProfiles() );
            projectBuildingRequest.setProcessPlugins( true );
            projectBuildingRequest.setBuildStartTime( getStartTime() );
        }

        return projectBuildingRequest;
    }

    public MavenExecutionRequest addProfile( final Profile profile )
    {
        if ( profile == null )
        {
            throw new IllegalArgumentException( "profile missing" );
        }

        for ( final Profile p : getProfiles() )
        {
            if ( p.getId() != null && p.getId().equals( profile.getId() ) )
            {
                return this;
            }
        }

        getProfiles().add( profile );

        return this;
    }

    public RepositoryCache getRepositoryCache()
    {
        return repositoryCache;
    }

    public MavenExecutionRequest setRepositoryCache( final RepositoryCache repositoryCache )
    {
        this.repositoryCache = repositoryCache;

        return this;
    }

    public ExecutionListener getExecutionListener()
    {
        return executionListener;
    }

    public MavenExecutionRequest setExecutionListener( final ExecutionListener executionListener )
    {
        this.executionListener = executionListener;

        return this;
    }

    public String getThreadCount()
    {
        return threadCount;
    }

    public void setThreadCount( final String threadCount )
    {
        this.threadCount = threadCount;
    }

    public boolean isThreadConfigurationPresent()
    {
        return getThreadCount() != null;
    }

    public boolean isPerCoreThreadCount()
    {
        return perCoreThreadCount;
    }

    public void setPerCoreThreadCount( final boolean perCoreThreadCount )
    {
        this.perCoreThreadCount = perCoreThreadCount;
    }

    public WorkspaceReader getWorkspaceReader()
    {
        return workspaceReader;
    }

    public MavenExecutionRequest setWorkspaceReader( final WorkspaceReader workspaceReader )
    {
        this.workspaceReader = workspaceReader;
        return this;
    }

    public boolean isCacheTransferError()
    {
        return cacheTransferError;
    }

    public MavenExecutionRequest setCacheTransferError( final boolean cacheTransferError )
    {
        this.cacheTransferError = cacheTransferError;
        return this;
    }

    public boolean isCacheNotFound()
    {
        return cacheNotFound;
    }

    public MavenExecutionRequest setCacheNotFound( final boolean cacheNotFound )
    {
        this.cacheNotFound = cacheNotFound;
        return this;
    }

    private MirrorRoutingTable mirrorRoutingTable;

    public synchronized MirrorRoutingTable getMirrorRoutingTable()
    {
        if ( mirrorRoutingTable == null )
        {
            mirrorRoutingTable = new MirrorRoutingTable();
        }

        return mirrorRoutingTable;
    }

    public MavenExecutionRequest setMirrorRoutingTable( final MirrorRoutingTable mirrorRoutingTable )
    {
        this.mirrorRoutingTable = mirrorRoutingTable;
        return this;
    }

}
