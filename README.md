
# utils-public
Public utilities

## Test SSL Connection with New Relic

### Compile the Program

```sh
javac SSLConnectionTest.java
```

### Run the Program

Pass the New Relic account ID and the Insights Insert key as command-line arguments when running the program.

```sh
java SSLConnectionTest <NewRelicAccountID> <InsightsInsertKey>
```

Replace `<NewRelicAccountID>` with your actual New Relic account ID and `<InsightsInsertKey>` with your actual Insights Insert key.

### Example Command

```sh
java SSLConnectionTest 1234567 YOUR_INSIGHTS_INSERT_KEY
```

This program will attempt to establish an SSL connection to the New Relic Insights endpoint using the provided account ID and Insights Insert key.

