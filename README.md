# Adaptive Entity Hitboxes

Adaptive Entity Hitboxes is a Fabric 1.21.1 client mod intended to approximate the visible bounds
of custom Entity Model Features (EMF) models with a single Minecraft entity bounding box.

## Current status

The current development checkpoint includes:

- Fabric client entry point
- Fabric mod metadata
- EMF and ETF development dependencies resolved through Modrinth Maven
- Java 21 and Minecraft 1.21.1 Gradle configuration
- Detection of active custom EMF model variants
- Combined neutral-pose and current rendered-pose model-space bounds measurement
- Support for renderer-level scaling such as cave spiders
- Client-side application of measured width and height to adult non-player living entities

Version 1.0 applies an origin-centered side-to-side width and the measured height to each matching
rendered entity and refreshes its client-side bounding box. Dimensions never shrink below vanilla values, and model
depth is not converted into width, preventing long quadrupeds from receiving oversized square
hitboxes. The centered width also includes model parts offset to either side of the entity origin.
Combining neutral and rendered poses includes JEM parts whose placement is controlled by animation,
such as separately positioned humanoid heads, without continuously resizing the hitbox every frame.
Vanilla eye height and attachment positions remain unchanged. Players and baby entities
are excluded. Resource-pack reloads clear cached bounds; disabling a custom model restores vanilla
dimensions on its next render, while enabling a model measures and applies it on its next render.

## AI-assisted development

The source code for this project was generated with assistance from generative AI. It has been
reviewed, built, and tested by the project maintainer, but users and contributors should still
independently review the code and report any problems they find. See [AI_DISCLOSURE.md](AI_DISCLOSURE.md)
for additional details.
