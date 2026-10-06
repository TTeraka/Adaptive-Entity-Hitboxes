# Adaptive Entity Hitboxes

Adaptive Entity Hitboxes is a Fabric 1.21.1 client mod intended to approximate the visible bounds
of custom Entity Model Features (EMF) models with a single Minecraft entity bounding box.

## Current status

The current development checkpoint includes:

- Fabric client entry point
- Fabric mod metadata
- Local EMF and ETF development dependencies
- Java 21 and Minecraft 1.21.1 Gradle configuration
- Detection of active custom EMF model variants
- Model-space bounds measurement and comparison logging

Entity dimensions are not changed yet. Version 0.2 measures the geometry EMF renders for each
observed model variant and logs its width, height, depth, suggested entity width, and the entity's
vanilla dimensions. These measurements must be validated in game before they are applied as hitboxes.

## AI-assisted development

The source code for this project was generated with assistance from generative AI. It has been
reviewed, built, and tested by the project maintainer, but users and contributors should still
independently review the code and report any problems they find. See [AI_DISCLOSURE.md](AI_DISCLOSURE.md)
for additional details.
