# Implementation notes

## Measurement boundary

`SpeedTestEngine` owns the test lifecycle. `AppViewModel` only turns events into UI state and persists the final immutable `SpeedTestResult`.

`SpeedMath.ema()` is for visual smoothness only. `ResultAggregator` collects raw samples and constructs the final result once.

## Cancellation

The ViewModel cancels the collection job and calls `SpeedTestEngine.cancel()`. The download and upload testers cancel their child coroutines in `finally`. The next hardening pass should optionally wrap OkHttp calls with cancellable callbacks on devices where stop latency needs to be minimized.

## Demo mode

Demo mode exists specifically so Compose Preview / UI work can happen without a live server. It should remain available during development and can be removed from production only after the real-node environment has full integration coverage.
