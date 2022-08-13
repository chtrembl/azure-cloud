# 17 - Configure SignalR to send Real-Time Analytics to Client Browser

Just imagine you are developing a progressive/single page web application that requires real-time data. Sure you can have your end users refresh the page, and reload/re render the data and html, server side, but that would be a less than idea user experience. You could also build out the control plane to facilitate client/server invocations using WebSockets, but that would require you to integrate/write more pub/sub code to integrate with a centralized cache/hub. Another option is [SignalR](https://docs.microsoft.com/en-us/aspnet/signalr/overview/getting-started/introduction-to-signalr), a technology that simplifies the process of adding real-time web functionality to your application that allows your server code to send content to connected clients instantly as it becomes available, rather than having the server wait for a client to request new data, freeing up more time for you to be productive and write code that provides business value.

**This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)**

Things you can now do now with this guide

☑️ SignalR