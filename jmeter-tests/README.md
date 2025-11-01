# JMeter Performance Tests

This directory contains Apache JMeter performance tests for the Cashi Payment API.

## Test Plan Overview

**File**: `PaymentAPI_LoadTest.jmx`

### What It Tests
- **Endpoint**: `POST /payments`
- **Concurrent Users**: 5 users
- **Iterations**: 10 requests per user (50 total requests)
- **Ramp-up Time**: 5 seconds
- **Think Time**: Random 0.5-1.5 seconds between requests

### Test Scenarios
1. **Valid Payment Submissions** - Tests successful payment processing
2. **Response Time** - Measures API latency under load
3. **Throughput** - Requests per second
4. **Error Rate** - Percentage of failed requests
5. **Firebase Integration** - Verifies data is saved to Firestore

## Prerequisites

### 1. Install Apache JMeter

**macOS (Homebrew)**:
```bash
brew install jmeter
```

**Manual Installation**:
1. Download from [https://jmeter.apache.org/download_jmeter.cgi](https://jmeter.apache.org/download_jmeter.cgi)
2. Extract to a directory
3. Add `bin/` to your PATH

**Verify Installation**:
```bash
jmeter --version
```

### 2. Start the Server

The Cashi server must be running with Firebase configured:

```bash
# From project root
./gradlew :server:run
```

**Important**: Server needs `serviceAccountKey.json` in project root!

### 3. Verify Server is Running

```bash
curl http://localhost:8080/
# Expected: "Cashi Payment Server is running ✅"
```

## Running the Tests

### Option 1: GUI Mode (For Development/Debugging)

```bash
# From project root
jmeter -t jmeter-tests/PaymentAPI_LoadTest.jmx
```

Then click the green **Start** button (▶️) in JMeter GUI.

**View Results**:
- **View Results Tree** - Individual request/response details
- **Summary Report** - Overall statistics
- **Graph Results** - Visual performance graph

### Option 2: CLI Mode (For CI/CD)

```bash
# From project root
jmeter -n -t jmeter-tests/PaymentAPI_LoadTest.jmx \
       -l jmeter-tests/results/test_results.jtl \
       -e -o jmeter-tests/results/html-report
```

**Parameters**:
- `-n` - Non-GUI mode
- `-t` - Test plan file
- `-l` - Results log file (.jtl)
- `-e` - Generate HTML report
- `-o` - Output directory for HTML report

**View Results**:
```bash
open jmeter-tests/results/html-report/index.html
```

## Interpreting Results

### Key Metrics

#### 1. **Response Time (Latency)**
```
Average: < 500ms   ✅ Excellent
Average: 500-1s    ⚠️  Acceptable
Average: > 1s      ❌ Needs optimization
```

#### 2. **Throughput**
```
Requests/second: How many payments the server can process
Goal: > 10 requests/second for this test
```

#### 3. **Error Rate**
```
0%        ✅ Perfect
< 1%      ✅ Excellent
1-5%      ⚠️  Acceptable
> 5%      ❌ Investigation needed
```

#### 4. **95th Percentile Response Time**
```
< 1s      ✅ Excellent
1-2s      ⚠️  Acceptable
> 2s      ❌ Poor user experience
```

### Sample Output (CLI Mode)

```
summary +     50 in 00:00:10 =    5.0/s Avg:   198 Min:   150 Max:   450 Err:     0 (0.00%)
```

**Breakdown**:
- **50** requests completed
- **10 seconds** total duration
- **5.0/s** throughput (requests per second)
- **198ms** average response time
- **150ms** minimum response time
- **450ms** maximum response time
- **0 errors** (0.00% error rate)

## Customizing Tests

### Change Concurrent Users

Edit `PaymentAPI_LoadTest.jmx`:
```xml
<stringProp name="ThreadGroup.num_threads">10</stringProp>  <!-- Change from 5 to 10 -->
```

Or use JMeter variables:
```bash
jmeter -n -t jmeter-tests/PaymentAPI_LoadTest.jmx \
       -JUSERS=10 \
       -JLOOPS=20 \
       -l jmeter-tests/results/test_results.jtl
```

### Change Server URL

Edit variables in test plan:
```xml
<elementProp name="SERVER_HOST" ...>
  <stringProp name="Argument.value">your-server.com</stringProp>
</elementProp>
<elementProp name="SERVER_PORT" ...>
  <stringProp name="Argument.value">443</stringProp>
</elementProp>
```

### Add More Test Data

Edit `payment_test_data.csv`:
```csv
recipientEmail,amount,currency
newuser@example.com,999.99,USD
```

JMeter will cycle through all rows during testing.

## Test Data

**File**: `payment_test_data.csv`

Contains 10 sample payments:
- Mix of USD and EUR currencies
- Amounts ranging from $75.25 to $1000.00
- Valid email addresses

**How it works**:
- JMeter reads one row per request
- Automatically cycles back to start when data runs out
- 50 requests will use each row 5 times

## Troubleshooting

### Server Connection Refused
```
Error: java.net.ConnectException: Connection refused
```
**Solution**: Make sure server is running on `http://localhost:8080`

### Firebase Errors in Server
```
Error: Firebase Admin SDK not initialized
```
**Solution**: Add `serviceAccountKey.json` to project root

### High Error Rate
```
Error Rate: 50% (25/50 failed)
```
**Check**:
1. Server logs for exceptions
2. Firebase quota limits
3. Invalid test data in CSV
4. Network issues

### Slow Response Times
```
Average: 5000ms (5 seconds)
```
**Possible causes**:
1. Firebase write latency (first-time setup)
2. Cold start issues
3. Database indexing
4. Network latency

**Solution**: Run multiple test iterations to warm up caches

## Advanced Testing

### Stress Test (Increase Load Until Failure)

```bash
# 50 users, 20 iterations = 1000 requests
jmeter -n -t jmeter-tests/PaymentAPI_LoadTest.jmx \
       -JUSERS=50 \
       -JLOOPS=20 \
       -l jmeter-tests/results/stress_test.jtl
```

### Spike Test (Sudden Traffic Surge)

Edit test plan:
- Set ramp-up time to `1` second
- Set users to `50`
- Run and observe if server handles sudden load

### Endurance Test (Long Duration)

```bash
# 5 users running for 1 hour
jmeter -n -t jmeter-tests/PaymentAPI_LoadTest.jmx \
       -JUSERS=5 \
       -JDURATION=3600 \
       -l jmeter-tests/results/endurance_test.jtl
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Run JMeter Tests
  run: |
    ./gradlew :server:run &
    sleep 10  # Wait for server startup
    jmeter -n -t jmeter-tests/PaymentAPI_LoadTest.jmx \
           -l results.jtl \
           -e -o jmeter-report

- name: Upload Results
  uses: actions/upload-artifact@v3
  with:
    name: jmeter-results
    path: jmeter-report/
```

## Expected Performance Baseline

Based on current implementation:

| Metric | Expected Value | Notes |
|--------|---------------|-------|
| Average Response Time | < 300ms | Without Firebase cold start |
| 95th Percentile | < 500ms | |
| Throughput | > 15 req/s | On local machine |
| Error Rate | 0% | With valid test data |
| Max Concurrent Users | 20+ | Before degradation |

**Note**: First run may be slower due to Firebase cold start. Run tests 2-3 times for accurate baseline.

## Files

```
jmeter-tests/
├── README.md                      # This file
├── PaymentAPI_LoadTest.jmx        # JMeter test plan
├── payment_test_data.csv          # Test data
└── results/                       # Test results (generated)
    ├── test_results.jtl           # Raw results
    └── html-report/               # HTML dashboard
        └── index.html
```

## Next Steps

1. Run baseline tests to establish performance metrics
2. Optimize based on results
3. Set up continuous performance monitoring
4. Add more complex scenarios (invalid payments, error handling)
5. Test database query performance
6. Monitor Firebase usage/costs under load