
# Running Windows and Linux Containers on Windows Server 2019

Below are steps taken to switch between Windows Containers and Linux Containers on Windows Server 2019 using Docker in [Hyper-V isolation mode.](https://docs.microsoft.com/en-us/virtualization/windowscontainers/manage-containers/hyperv-container) These steps were performed on a Windows Server 2019 VM in Azure  (sku Standard D2 v3) and assume you have the equivalent running. 

*Note, for more information on running Docker in Process Isolation or Hyper-V Isolation, on a per container basis, **without** switching between Windows or Linux, see [here](https://success.docker.com/article/hyper-v-containers-fail-to-start)*

1. **Install Hyper-V on your Windows Server 2019 Server**

	Run the PowerShell as Administrator and execute the following command:
    ```
    Enable-WindowsOptionalFeature –Online -FeatureName Microsoft-Hyper-V –All -NoRestart
    Install-WindowsFeature RSAT-Hyper-V-Tools -IncludeAllSubFeature
   ```
   ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/1.png?raw=true)
   **Restart VM**
2. **Enable the containers feature in Windows Server 2019**

	Run the PowerShell as Administrator and execute the following command:
	```
	Install-Module -Name DockerMsftProvider -Repository PSGallery -Force
	```
	**Select Yes**
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/2.png?raw=true)
3. **Install Docker**
	
	Run the PowerShell as Administrator and execute the following command:
	```
	Install-Package -Name docker -ProviderName DockerMsftProvider
	```
	**Select Yes to All and Restart VM**
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/3.png?raw=true)

	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/4.png?raw=true)
	
	*Note, The following command is used to list version details*
	```
	docker version
	```
	
	*Note, The following command is used to force updates*
	```
	Install-Package -Name Docker -ProviderName DockerMsftProvider -Update -Force  
	Start-Service Docker
	```
4. **Start Docker**
	
	Run the PowerShell as Administrator and execute the following command:
	```
	Start-Service Docker
	```
5. **Pull a Windows image** 
	
	Run the PowerShell as Administrator and execute the following command:
	```
	docker pull microsoft/dotnet-samples:dotnetapp-nanoserver-1809
	```
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/5.png?raw=true)
	
	List the images
	```
	docker image ls
	```
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/6.png?raw=true)
	
	Run a windows image
	```
	docker run <containerid>
	```
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/7.png?raw=true)
6. **Pull a Linux image**
	
	Run the PowerShell as Administrator and execute the following command:
	```
	docker pull ubuntu:18.04
	```
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/8.png?raw=true)
	
	***Note, it fails, that is because we haven't yet enabled Linux support, DockerMsftProvider that we installed is not sufficient, we will need the Preview Provider for Linux support***
7.  **Uninstall the default DockerMSFTProvider package and Install the Preview DockerMSFTProvider package**
	
	Run the PowerShell as Administrator and execute the following command:
	```
	Uninstall-Package -Name docker -ProviderName DockerMSFTProvider
	```
	Run the PowerShell as Administrator and execute the following command:
	```
	Install-Module DockerProvider  
	Install-Package Docker -ProviderName DockerProvider -RequiredVersion preview
	```
	Run the PowerShell as Administrator and execute the following command:
	```
	[Environment]::SetEnvironmentVariable(“LCOW_SUPPORTED”, “1”, “Machine”)
	```
	Run the PowerShell as Administrator and execute the following command:
	```
	Restart-Service docker
	```
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/9.png?raw=true)
	
8. **Try pulling a Linux image again**
	
	Run the PowerShell as Administrator and execute the following command:
	```
	docker pull ubuntu:18.04
	```

	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/10.png?raw=true)
	
	***Note, It works now!***
	
	Run the PowerShell as Administrator and execute the following command:
	```
	docker image ls
	```
	Run the PowerShell as Administrator and execute the following command:
	```
	docker run <containerid> echo "hello from ubuntu"
	```
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/11.png?raw=true)	
	
	Your now running a Linux container.
	
	***Note, you will notice only Linux images here. You will have to switch back to Windows with the following command:***
	
	Run the PowerShell as Administrator and execute the following command:
	
	```
	[Environment]::SetEnvironmentVariable(“LCOW_SUPPORTED”, “$null”, “Machine”)
	```

	Run the PowerShell as Administrator and execute the following command:
	
	```
	Restart-Service docker
	```
	
	***Note, now if you list the Docker images you will see the Windows images and you can run them.***
		
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/ws2019containers/12.png?raw=true)
