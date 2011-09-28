## Changes in 1.0.2
 * Changed SgdMapper from block-based to stream-based processing
 * Interface `StreamMapper` now only conists of `init()`, `process(Data)` and `finish()`

## Changes in 1.0.1
 * Added support for parameter `maxThreads` using system properties
 * Fixed missing test-error value in result file