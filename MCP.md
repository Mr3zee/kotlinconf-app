# MCP

All docs: https://modelcontextprotocol.io/

We used MCP config for [Claude Desktop](https://modelcontextprotocol.io/quickstart/user).
Raycast and JB AI Assistant can reuse the same config.

```json5
{
  "mcpServers": {
    // Adds connection to JetBrains IDE MCP server
    "jetbrains": {
      "command": "npx",
      "args": [
        "-y",
        "@jetbrains/mcp-proxy"
      ]
    }, 
    // Local proxy KotlinConf server
    "kotlin-conf": {
      "command": "/Users/<USER>/.local/bin/mcp-proxy",
      "args": [
        "http://0.0.0.0:8080/sse"
      ]
    },
    // Github MCP
    "github": {
      "command": "docker",
      "args": [
        "run",
        "-i",
        "--rm",
        "-e",
        "GITHUB_PERSONAL_ACCESS_TOKEN",
        "ghcr.io/github/github-mcp-server"
      ],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": "<YOUR_GH_KEY>"
      }
    },
    // Google Maps MCP
    "google-maps": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-google-maps"
      ],
      "env": {
        "GOOGLE_MAPS_API_KEY": "<YOUR_MAPS_KEY>"
      }
    }
  }
}
```

## KotlinConf App

To use KotlinConf App — check the latest commits in this repo.
Additionally, you need to use [mcp-proxy](https://github.com/sparfenyuk/mcp-proxy) MCP server,
as KotlinConf App uses [SSH](https://modelcontextprotocol.io/docs/concepts/transports#server-sent-events-sse)
as transport,
but Claude desktop uses [STDIO](https://modelcontextprotocol.io/docs/concepts/transports#standard-input%2Foutput-stdio)
by default.

After you install `mcp-proxy`, use `where` command to find its path for the `"command"` argument:
```
"command": "/Users/<USER>/.local/bin/mcp-proxy"
```

## GitHub

Instructions: https://github.com/github/github-mcp-server

Remember to start docker!

## Google Maps

Instructions: https://github.com/modelcontextprotocol/servers/tree/main/src/google-maps

## JetBrains

You can also make your IDE an MCP server. 
For that—install this plugin: https://plugins.jetbrains.com/plugin/26071-mcp-server

# Presentation

Presentation is [here](MCP%20in%20Kotlin.pdf)
