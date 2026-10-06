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
- Neutral-pose model-space bounds measurement
- Support for renderer-level scaling such as cave spiders
- Client-side application of measured width and height to adult non-player living entities

Version 0.3 applies the measured dimensions to each matching rendered entity and refreshes its
client-side bounding box. Vanilla eye height and attachment positions remain unchanged. Players and
baby entities are excluded from this first functional checkpoint.

## AI-assisted development

The source code for this project was generated with assistance from generative AI. It has been
reviewed, built, and tested by the project maintainer, but users and contributors should still
independently review the code and report any problems they find. See [AI_DISCLOSURE.md](AI_DISCLOSURE.md)
for additional details.
