# Adaptive Entity Hitboxes

Adaptive Entity Hitboxes is a Fabric 1.21.1 client mod intended to approximate the visible bounds
of custom Entity Model Features (EMF) models with a single Minecraft entity bounding box.

## Current status

This repository currently contains only the project scaffold:

- Fabric client entry point
- Fabric mod metadata
- EMF declared as a required runtime mod
- Java 21 and Minecraft 1.21.1 Gradle configuration

No hitbox behavior is implemented yet. The next development step is to identify a stable EMF 3.3.9
integration point for obtaining the active model root, then calculate and cache conservative model
bounds before changing any entity dimensions.

## AI-assisted development

The source code for this project was generated with assistance from generative AI. It has been
reviewed, built, and tested by the project maintainer, but users and contributors should still
independently review the code and report any problems they find. See [AI_DISCLOSURE.md](AI_DISCLOSURE.md)
for additional details.
