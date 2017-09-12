package deeone.jmeter.protocol.bigtable.sampler;

import com.google.bigtable.v2.ReadRowsRequest;
import com.google.bigtable.v2.Row;
import com.google.bigtable.v2.RowRange;
import com.google.bigtable.v2.RowSet;
import com.google.cloud.bigtable.grpc.BigtableDataClient;
import com.google.cloud.bigtable.grpc.scanner.ResultScanner;
import com.google.common.base.Charsets;
import com.google.protobuf.ByteString;
import deeone.jmeter.protocol.bigtable.config.BigtableSessionConfig;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;

public class BigTableReadRowsSampler extends AbstractJavaSamplerClient {

    private static final String ENCODING = "UTF-8";

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("startKey", "");
        defaultParameters.addArgument("endKey", "");
        defaultParameters.addArgument("tableName", "");
        return defaultParameters;
    }

    public SampleResult runTest(JavaSamplerContext context) {

        SampleResult result = newSampleResult();
        try {

            final String startKey = context.getParameter("startKey");
            final String endKey = context.getParameter("endKey");
            final String tableName = context.getParameter("tableName");
            final RowRange rowRange = RowRange.newBuilder()
                    .setStartKeyOpen(ByteString.copyFrom(startKey, Charsets.UTF_8))
                    .setEndKeyOpen(ByteString.copyFrom(endKey, Charsets.UTF_8))
                    .build();
            final RowSet rowSet = RowSet.newBuilder()
                    .addRowRanges(rowRange)
                    .build();
            final ReadRowsRequest readRowsRequest = ReadRowsRequest.newBuilder()
                    .setTableName(tableName)
                    .setRows(rowSet)
                    .build();

            result.setSentBytes(readRowsRequest.getSerializedSize());
            final String message = readRowsRequest.toString();

            final BigtableDataClient dataClient = BigtableSessionConfig.getDataClient();

            sampleResultStart(result, message);

            final ResultScanner<Row> rowResultScanner = dataClient.readRows(readRowsRequest);
            int rowCount = 0;
            long size=0L;
//            StringBuilder stringBuilder = new StringBuilder();
//            JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();
            for (Row r = rowResultScanner.next(); r != null; r = rowResultScanner.next()) {
                // consume all rows
                size += r.getSerializedSize();
//                printer.appendTo(r, stringBuilder);
                rowCount++;
            }
//            sampleResultSuccess(result, stringBuilder.toString());
            result.setBodySize(size);
            sampleResultSuccess(result, String.valueOf(rowCount));

        } catch (Exception e) {
            sampleResultFailed(result, "500", e);
        }

        return result;
    }

    /**
     * Factory for creating new {@link SampleResult}s.
     */
    private SampleResult newSampleResult() {
        SampleResult result = new SampleResult();
        result.setDataEncoding(ENCODING);
        result.setDataType(SampleResult.TEXT);
        return result;
    }


    /**
     * Start the sample request and set the {@code samplerData} to {@code data}.
     *
     * @param result
     *          the sample result to update
     * @param data
     *          the request to set as {@code samplerData}
     */
    private void sampleResultStart(SampleResult result, String data) {
        result.setSamplerData(data);
        result.sampleStart();
    }



    /**
     * Mark the sample result as {@code end}ed and {@code successful} with an "OK" {@code responseCode},
     * and if the response is not {@code null} then set the {@code responseData} to {@code response},
     * otherwise it is marked as not requiring a response.
     *
     * @param result sample result to change
     * @param response the successful result message, may be null.
     */
    private void sampleResultSuccess(SampleResult result, @Nullable String response) {
        result.sampleEnd();
        result.setSuccessful(true);
        result.setResponseCodeOK();
        if (response != null) {
            result.setResponseData(response, ENCODING);
        }
        else {
            result.setResponseData("No response required", ENCODING);
        }
    }

    /**
     * Mark the sample result as @{code end}ed and not {@code successful}, and set the
     * {@code responseCode} to {@code reason}.
     *
     * @param result the sample result to change
     * @param reason the failure reason
     */
    private void sampleResultFailed(SampleResult result, String reason) {
        result.sampleEnd();
        result.setSuccessful(false);
        result.setResponseCode(reason);
    }

    /**
     * Mark the sample result as @{code end}ed and not {@code successful}, set the
     * {@code responseCode} to {@code reason}, and set {@code responseData} to the stack trace.
     *
     * @param result the sample result to change
     * @param exception the failure exception
     */
    private void sampleResultFailed(SampleResult result, String reason, Exception exception) {
        sampleResultFailed(result, reason);
        result.setResponseMessage("Exception: " + exception);
        result.setResponseData(getStackTrace(exception), ENCODING);
    }

    /**
     * Return the stack trace as a string.
     *
     * @param exception the exception containing the stack trace
     * @return the stack trace
     */
    private String getStackTrace(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
