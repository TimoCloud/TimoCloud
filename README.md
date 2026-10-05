[![Build](https://github.com/TimoCloud/TimoCloud/actions/workflows/build.yml/badge.svg)](https://github.com/TimoCloud/TimoCloud/actions/workflows/build.yml) <!--[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b341b86dc4704d59b54f059d0cf6d5d1)](https://www.codacy.com/project/TimoCrafter/TimoCloud/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=TimoCloud/TimoCloud&amp;utm_campaign=Badge_Grade_Dashboard)-->
# TimoCloud
TimoCloud is a Minecraft server/proxy management system ("Cloud System"). It will care about keeping online enough servers/proxies of every kind. But it's more than that. Thank to its algorithms, you'll never have to care again about servers or resources. TimoCloud automatically chooses servers with low CPU usage and enough available RAM to start your instances. And its integrated *Flow*-System makes updating templates easier than ever before: Edit one file in a server template, and the update will automatically be deployed to all bases (a.k.a. "Wrapper"). But of course, only the changed files will get updated.

## Features
TimoCloud is full of innovative features. Here is a list of the most important ones:

 - **Automatic and dynamic starting** of Minecraft servers and BungeeCord/Velocity proxies - depending on the current online player amount.
 - **Automatic and dynamic selection** of a virtual machine (server) with **enough available resources** for an instance which is getting started.
 - **Powerful API**, synchronized over your whole network in real-time, also offering async methods with callbacks. Everything that can be done via commands also has a corresponding API method.
 - **Secure Communication**: TimoCloud uses RSA and AES encryption to securely communicate between Core, Bases, servers and proxies
 - **High efficiency**: Efficiency is one of our major goals: While using powerful software such as *screen* and *Netty*, TimoCloud tries to keep your servers' resource usage as little as possible
 - **Multi-Root**: Use one *TimoCloudCore* instance in combination with as many bases (a.k.a. "Wrapper") as you want. 
 - **Multi-Proxy**: TimoCloud is standalone and does not only start Bukkit/Spigot servers for you, but also BungeeCord/Velocity proxies
 - **Player distribution:** The players are distributed to the different proxies thanks to our cord - depending on algorithms you can choose. 
 - **Beautiful, live-updating sign system**: Use the integrated server join sign system to let players access all your servers. Signs are dynamic - that means only _free_ servers will be displayed - and can be animated.

## Download

Latest stable build: see the [latest release](https://github.com/TimoCloud/TimoCloud/releases/latest).

## Setup
See the [Wiki](https://github.com/TimoCloud/TimoCloud/wiki)

## Building from source
**Please use Java 8 to build the project.**
```
git clone https://github.com/TimoCloud/TimoCloud
cd TimoCloud
mvn clean package
```

The built JAR will be available at `TimoCloud-Universal/target/TimoCloud.jar`.

## Support
For support, please join our [Discord](https://discord.gg/RTNn4SE).
