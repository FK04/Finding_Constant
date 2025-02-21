import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

public class ShamirSecretSharing{

    public static void main(String[] args) {
        try {
            JSONParser jsonParser = new JSONParser();

            JSONObject test1 = (JSONObject) jsonParser.parse(new FileReader("testcase1.json"));
            JSONObject test2 = (JSONObject) jsonParser.parse(new FileReader("testcase2.json"));

            System.out.println("Secret for Test 1: " + calculateSecret(test1));
            System.out.println("Secret for Test 2: " + calculateSecret(test2));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static BigInteger calculateSecret(JSONObject test) {
        JSONObject info = (JSONObject) test.get("keys");
        if (info == null) {
            System.out.println("Missing 'keys' section in JSON.");
            return BigInteger.ZERO;
        }

        int numPointsNeeded = Integer.parseInt(info.get("k").toString());

        HashMap<Integer, BigInteger> pointsMap = new HashMap<>();

        for (Object key : test.keySet()) {
            String keyString = key.toString();
            if (!keyString.equals("keys")) {
                JSONObject pointInfo = (JSONObject) test.get(keyString);
                if (pointInfo == null) {
                    System.out.println("Missing root " + keyString + " in JSON.");
                    return BigInteger.ZERO;
                }

                String baseString = pointInfo.get("base").toString();
                String valueString = pointInfo.get("value").toString();
                BigInteger yValue = new BigInteger(valueString, Integer.parseInt(baseString));
                pointsMap.put(Integer.parseInt(keyString), yValue);
            }
        }

        BigInteger secret = BigInteger.ZERO;
        int count = 0;

        for (HashMap.Entry<Integer, BigInteger> entry : pointsMap.entrySet()) {
            if (count >= numPointsNeeded) break;
            int x = entry.getKey();
            BigInteger y = entry.getValue();
            BigInteger top = y;
            BigInteger bottom = BigInteger.ONE;

            for (HashMap.Entry<Integer, BigInteger> innerEntry : pointsMap.entrySet()) {
                int xInner = innerEntry.getKey();
                if (xInner != x) {
                    top = top.multiply(BigInteger.valueOf(-xInner));
                    bottom = bottom.multiply(BigInteger.valueOf(x - xInner));
                }
            }

            secret = secret.add(top.divide(bottom));
            count++;
        }

        return secret;
    }
}

