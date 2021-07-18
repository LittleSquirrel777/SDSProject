package com.iman.sds.chain;

import com.alipay.mychain.sdk.api.MychainClient;
import com.alipay.mychain.sdk.api.callback.IEventCallback;
import com.alipay.mychain.sdk.api.env.ClientEnv;
import com.alipay.mychain.sdk.api.env.ISslOption;
import com.alipay.mychain.sdk.api.env.SignerOption;
import com.alipay.mychain.sdk.api.env.SslBytesOption;
import com.alipay.mychain.sdk.api.logging.AbstractLoggerFactory;
import com.alipay.mychain.sdk.api.logging.ILogger;
import com.alipay.mychain.sdk.api.utils.ConfidentialUtil;
import com.alipay.mychain.sdk.api.utils.Utils;
import com.alipay.mychain.sdk.common.VMTypeEnum;
import com.alipay.mychain.sdk.crypto.MyCrypto;
import com.alipay.mychain.sdk.crypto.hash.Hash;
import com.alipay.mychain.sdk.crypto.keyoperator.Pkcs8KeyOperator;
import com.alipay.mychain.sdk.crypto.keypair.Keypair;
import com.alipay.mychain.sdk.crypto.signer.SignerBase;
import com.alipay.mychain.sdk.domain.account.Identity;
import com.alipay.mychain.sdk.domain.event.EventModelType;
import com.alipay.mychain.sdk.errorcode.ErrorCode;
import com.alipay.mychain.sdk.message.Message;
import com.alipay.mychain.sdk.message.event.PushContractEvent;
import com.alipay.mychain.sdk.message.transaction.AbstractTransactionRequest;
import com.alipay.mychain.sdk.message.transaction.TransactionReceiptResponse;
import com.alipay.mychain.sdk.message.transaction.confidential.ConfidentialRequest;
import com.alipay.mychain.sdk.message.transaction.contract.*;
import com.alipay.mychain.sdk.type.BaseFixedSizeUnsignedInteger;
import com.alipay.mychain.sdk.utils.ByteUtils;
import com.alipay.mychain.sdk.utils.IOUtil;
import com.alipay.mychain.sdk.utils.RandomUtil;
import com.alipay.mychain.sdk.vm.EVMOutput;
import com.alipay.mychain.sdk.vm.EVMParameter;
import com.iman.sds.entity.ScDescription;
import com.iman.sds.entity.Score;
import com.iman.sds.entity.Sensor;
import com.iman.sds.entity.SensorData;
import com.iman.sds.po.AddLogParam;
import com.iman.sds.po.LogDataParam;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Chris
 * @date 2021/5/29 18:22
 * @Email:gang.wu@nexgaming.com
 */
public class JRContractDemo {
    private static String contractCodeString = "0x608060405234801561001057600080fd5b50612ed5806100206000396000f30060806040526004361061008e576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063491cfe6e146100935780634e958d651461011e5780636275d125146101b05780636bf3638a1461029c5780637a6a18801461039c578063800da21e146104bd5780639165f638146105bd578063b4eca9d7146106ac575b600080fd5b34801561009f57600080fd5b5061010460048036038101908080359060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610798565b604051808215151515815260200191505060405180910390f35b34801561012a57600080fd5b5061018f60048036038101908080359060200190929190803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061097a565b60405180831515151581526020018281526020019250505060405180910390f35b3480156101bc57600080fd5b5061022160048036038101908080359060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610ae0565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610261578082015181840152602081019050610246565b50505050905090810190601f16801561028e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156102a857600080fd5b5061032160048036038101908080359060200190929190803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929080359060200190929190803590602001909291905050506111f4565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610361578082015181840152602081019050610346565b50505050905090810190601f16801561038e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156103a857600080fd5b506104a360048036038101908080359060200190929190803590602001908201803590602001908080601f0160208091040260200160";
    private static byte[] contractCode = ByteUtils.hexStringToBytes(contractCodeString); //CreditManager

    //upgrade
    private static String contractUpdateCodeString = "60806040526004361061006d576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680631b3c4fab1461007257806357fce39d14610187578063af7c102c146101b2578063b2628df8146101f3578063d448601914610242575b600080fd5b34801561007e57600080fd5b50610087610291565b60405180868152602001806020018060200185151515158152602001848152602001838103835287818151815260200191508051906020019080838360005b838110156100e15780820151818401526020810190506100c6565b50505050905090810190601f16801561010e5780820380516001836020036101000a031916815260200191505b50838103825286818151815260200191508051906020019080838360005b8381101561014757808201518184015260208101905061012c565b50505050905090810190601f1680156101745780820380516001836020036101000a031916815260200191505b5097505050505050505060405180910390f35b34801561019357600080fd5b5061019c610337565b6040518082815260200191505060405180910390f35b3480156101be57600080fd5b506101dd60048036038101908080359060200190929190505050610341565b6040518082815260200191505060405180910390f35b3480156101ff57600080fd5b50610228600480360381019080803590602001909291908035906020019092919050505061035e565b604051808215151515815260200191505060405180910390f35b34801561024e57600080fd5b506102776004803603810190808035906020019092919080359060200190929190505050610523565b604051808215151515815260200191505060405180910390f35b6000606080600080606080600080600033905060c89250600091506040805190810160405280600781526020017f6a72626c6f636b0000000000000000000000000000000000000000000000000081525094506040805190810160405280601a81526020017f32303231303533316a72626c6f636b636f6e7261637463616c6c000000000000815250935082858584849950995099509950995050505050509091929394565b6000600254905090565b600060036000838152602001908152602001600020549050919050565b6000600254331415156103d9576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260118152602001807f5065726d697373696f6e2064656e69656400000000000000000000000000000081525060200191505060405180910390fd5b6000548260015401131580156103f457506001548260015401135b80156104005750600082135b1515610474576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600e8152602001807f496e76616c69642076616c75652100000000000000000000000000000000000081525060200191505060405180910390fd5b8160036000858152602001908152602001600020600082825401925050819055508160016000828254019250508190555081837f9a46fdc9277c739031110f773b36080a9a2012d0b3eca1f5ed8a3403973e05576001546040518080602001838152602001828103825260048152602001807f64656d6f000000000000000000000000000000000000000000000000000000008152506020019250505060405180910390a36001905092915050565b6000816003600033815260200190815260200160002054121515156105b0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260138152602001807f62616c616e6365206e6f7420656e6f756768210000000000000000000000000081525060200191505060405180910390fd5b6000821380156105c257506000548213155b1515610636576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600e8152602001807f496e76616c69642076616c75652100000000000000000000000000000000000081525060200191505060405180910390fd5b81600360003381526020019081526020016000206000828254039250508190555081600360008581526020019081526020016000206000828254019250508190555060019050929150505600a165627a7a72305820929f39f5dfc978f05e029b986659fd7542e1009cbbb133b2bc009f8876b59c910029";
    private static byte[] contractUpdateCode = ByteUtils.hexStringToBytes(contractUpdateCodeString); //CreditManager

    /**
     * contract id
     */
    private static String callContractId = "watermonitoring";

    private static final String account = "littlesquirrel777";
    private static Identity userIdentity;
    private static Keypair userKeypair;

    /**
     * sdk client
     */
    private static MychainClient sdk;

    /**
     * client key password
     */
    private static String keyPassword = "20000818qwerLS.";
    /**
     * user password
     */
    private static String userPassword = "20000818qwerLS.";
    /**
     * host ip
     */

    private static String host = "47.103.163.48";

    /**
     * server port
     */
    private static int port = 18130;
//    private static int port = 18140;
    /**
     * trustCa password.
     */
    private static String trustStorePassword = "mychain";
    /**
     * mychain environment
     */
    private static ClientEnv env;
    /**
     * mychain is tee Chain
     */
    private static boolean isTeeChain = false;
    /**
     * tee chain publicKeys
     */
    private static List<byte[]> publicKeys = new ArrayList<byte[]>();
    /**
     * tee chain secretKey
     */
    private static String secretKey = "123456";


    public static void initMychainEnv() throws IOException {
        // any user key for sign message
        String userPrivateKeyFile = "user.key";
        userIdentity = Utils.getIdentityByName(account);
        Pkcs8KeyOperator pkcs8KeyOperator = new Pkcs8KeyOperator();
        userKeypair = pkcs8KeyOperator.load(IOUtil.inputStreamToByte(JRContractDemo.class.getClassLoader().getResourceAsStream(userPrivateKeyFile)), userPassword);

        // use publicKeys by tee
        if (isTeeChain) {
            Keypair keypair = new Pkcs8KeyOperator()
                    .loadPubkey(
                            IOUtil.inputStreamToByte(JRContractDemo.class.getClassLoader().getResourceAsStream("test_seal_pubkey.pem")));
            byte[] publicKeyDer = keypair.getPubkeyEncoded();
            publicKeys.add(publicKeyDer);
        }

        env = buildMychainEnv();
        ILogger logger = AbstractLoggerFactory.getInstance(JRContractDemo.class);
        env.setLogger(logger);
    }

    public static ClientEnv buildMychainEnv() throws IOException {
        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved(host, port);
//        String keyFilePath = "/certs/client.key";
//        String certFilePath = "/certs/client.crt";
//        String trustStoreFilePath = "/certs/trustCa";
        String keyFilePath = "client.key";
        String certFilePath = "client.crt";
        String trustStoreFilePath = "trustCa";
        // build ssl option
        ISslOption sslOption = new SslBytesOption.Builder()
                .keyBytes(IOUtil.inputStreamToByte(JRContractDemo.class.getClassLoader().getResourceAsStream(keyFilePath)))
                .certBytes(IOUtil.inputStreamToByte(JRContractDemo.class.getClassLoader().getResourceAsStream(certFilePath)))
                .keyPassword(keyPassword)
                .trustStorePassword(trustStorePassword)
                .trustStoreBytes(
                        IOUtil.inputStreamToByte(JRContractDemo.class.getClassLoader().getResourceAsStream(trustStoreFilePath)))
                .build();

        List<InetSocketAddress> socketAddressArrayList = new ArrayList<InetSocketAddress>();
        socketAddressArrayList.add(inetSocketAddress);

        List<SignerBase> signerBaseList = new ArrayList<SignerBase>();
        SignerBase signerBase = MyCrypto.getInstance().createSigner(userKeypair);
        signerBaseList.add(signerBase);
        SignerOption signerOption = new SignerOption();
        signerOption.setSigners(signerBaseList);
        return ClientEnv.build(socketAddressArrayList, sslOption, signerOption);
    }

    public static void initSdk() {
        sdk = new MychainClient();
        boolean initResult = sdk.init(env);
        if (!initResult) {
            exit("initSdk", "sdk init failed.");
        } else {
            System.out.println("sdk init success");
        }
    }

    public static String getErrorMsg(int errorCode) {
        int minMychainSdkErrorCode = ErrorCode.SDK_INTERNAL_ERROR.getErrorCode();
        if (errorCode < minMychainSdkErrorCode) {
            return ErrorCode.valueOf(errorCode).getErrorDesc();
        } else {
            return ErrorCode.valueOf(errorCode).getErrorDesc();
        }
    }

    public static void exit(String tag, String msg) {
        exit(String.format("%s error : %s ", tag, msg));
    }

    public static void exit(String msg) {
        System.out.println(msg);
        System.exit(0);
    }

    public static void signRequest(AbstractTransactionRequest request) {
        // sign request
        long ts = sdk.getNetwork().getSystemTimestamp();
        request.setTxTimeNonce(ts, BaseFixedSizeUnsignedInteger.Fixed64BitUnsignedInteger
                .valueOf(RandomUtil.randomize(ts + request.getTransaction().hashCode())), true);
        request.complete();
        sdk.getConfidentialService().signRequest(env.getSignerOption().getSigners(), request);
    }

    public static void deployContract() {
        EVMParameter contractParameters = new EVMParameter();
        String contractId = "wuda" + System.currentTimeMillis();

        // build DeployContractRequest
        DeployContractRequest request = new DeployContractRequest(userIdentity,
                Utils.getIdentityByName(contractId), contractCode, VMTypeEnum.EVM,
                contractParameters, BigInteger.ZERO);

        TransactionReceiptResponse deployContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);
            deployContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            deployContractResult = sdk.getContractService().deployContract(request);
        }

        // deploy contract
        if (!deployContractResult.isSuccess()
                || deployContractResult.getTransactionReceipt().getResult() != 0) {
            exit("deployContract",
                    getErrorMsg((int) deployContractResult.getTransactionReceipt().getResult()));
        } else {
            System.out.println("deploy contract success.contact id is " + contractId);
        }
    }

    //private static void callContractAddSensorCredit(int accountName,Integer amount)
    public static boolean callContractAddSensorCredit(Integer name, String addr) {
        EVMParameter parameters = new EVMParameter("AddSensor(uint256,string)");
        parameters.addUint(BigInteger.valueOf(name));
        parameters.addString(addr);

        // build CallContractRequest
        CallContractRequest request = new CallContractRequest(userIdentity,
                Utils.getIdentityByName(callContractId), parameters, BigInteger.ZERO, VMTypeEnum.EVM);

        TransactionReceiptResponse callContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);

            callContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            callContractResult = sdk.getContractService().callContract(request);
        }

        if (!callContractResult.isSuccess() || callContractResult.getTransactionReceipt().getResult() != 0) {
            System.out.println("callContract Error :" + getErrorMsg((int) callContractResult.getTransactionReceipt().getResult()));
            return false;
        } else {
            byte[] output = callContractResult.getTransactionReceipt().getOutput();
            if (output == null) {
                exit("call AddSensor function", "output failed");
                return false;
            } else {
                // decode return values
                EVMOutput contractReturnValues = new EVMOutput(ByteUtils.toHexString(output));
                System.out.println("call callContractIssueCredit success, response value: " + contractReturnValues.getBoolean());
                return true;
            }
        }
    }

    public static boolean callContractDataReceiveCredit(Sensor sensor, SensorData sensorData) {
        EVMParameter parameters = new EVMParameter("DataReceive(uint256,string,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256)");
//        parameters.addUint(BigInteger.valueOf(sensor.getId()));
        parameters.addUint(BigInteger.valueOf(sensor.getId()));
        parameters.addString(sensor.getAddress());
        parameters.addUint(BigInteger.valueOf(sensorData.getPh()));
        parameters.addUint(BigInteger.valueOf(sensorData.getChroma()));
        parameters.addUint(BigInteger.valueOf(sensorData.getSs()));
        parameters.addUint(BigInteger.valueOf(sensorData.getBod5()));
        parameters.addUint(BigInteger.valueOf(sensorData.getCod()));
        parameters.addUint(BigInteger.valueOf(sensorData.getAn()));
        parameters.addUint(BigInteger.valueOf(sensorData.getTn()));
        parameters.addUint(BigInteger.valueOf(sensorData.getTp()));
        parameters.addUint(BigInteger.valueOf(sensorData.getVp()));
        parameters.addUint(BigInteger.valueOf(sensorData.getToc()));

        // build CallContractRequest
        CallContractRequest request = new CallContractRequest(userIdentity,
                Utils.getIdentityByName(callContractId), parameters, BigInteger.ZERO, VMTypeEnum.EVM);

        TransactionReceiptResponse callContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);

            callContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            callContractResult = sdk.getContractService().callContract(request);
        }

        if (!callContractResult.isSuccess() || callContractResult.getTransactionReceipt().getResult() != 0) {
            System.out.println("callContract Error :" + getErrorMsg((int) callContractResult.getTransactionReceipt().getResult()));
            return false;
        } else {
            byte[] output = callContractResult.getTransactionReceipt().getOutput();
            if (output == null) {
                exit("call callContractDataReceiveCredit function", "output failed");
//                System.out.println("error1");
                return false;
            } else {
                // decode return values
                EVMOutput contractReturnValues = new EVMOutput(ByteUtils.toHexString(output));
                System.out.println("call callContractDataReceiveCredit success, response value: " + contractReturnValues.getBoolean());
                return true;
            }
        }
    }

    public static String callContractDataQueryCredit1(Sensor sensor) {
        EVMParameter parameters = new EVMParameter("DataQuery(uint256,string)");
        parameters.addUint(BigInteger.valueOf(sensor.getId()));
        parameters.addString(sensor.getAddress());

        // build CallContractRequest
        CallContractRequest request = new CallContractRequest(userIdentity,
                Utils.getIdentityByName(callContractId), parameters, BigInteger.ZERO, VMTypeEnum.EVM);

        TransactionReceiptResponse callContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);

            callContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            callContractResult = sdk.getContractService().callContract(request);
        }

        if (!callContractResult.isSuccess() || callContractResult.getTransactionReceipt().getResult() != 0) {
            System.out.println("callContract Error :" + getErrorMsg((int) callContractResult.getTransactionReceipt().getResult()));
            return "failed";
        } else {
            byte[] output = callContractResult.getTransactionReceipt().getOutput();
            if (output == null) {
                exit("call DataQuery function", "output failed");
                return "failed";
            } else {
                // decode return values
                EVMOutput contractReturnValues = new EVMOutput(ByteUtils.toHexString(output));
                String result = contractReturnValues.getString();
                System.out.println(String.format("call callContractDataQueryCredit function,log is %s", result));
                return result;
            }
        }
    }

    public static String callContractDataQueryCredit2(Sensor sensor, Date createTime, Date endTime) {
        EVMParameter parameters = new EVMParameter("DataQuery(uint256,string,uint256,uint256)");
        parameters.addUint(BigInteger.valueOf(sensor.getId()));
        parameters.addString(sensor.getAddress());
        parameters.addUint(BigInteger.valueOf(createTime.getTime()));
        parameters.addUint(BigInteger.valueOf(endTime.getTime()));
//        parameters.addString(endTime);

        // build CallContractRequest
        CallContractRequest request = new CallContractRequest(userIdentity,
                Utils.getIdentityByName(callContractId), parameters, BigInteger.ZERO, VMTypeEnum.EVM);

        TransactionReceiptResponse callContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);

            callContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            callContractResult = sdk.getContractService().callContract(request);
        }

        if (!callContractResult.isSuccess() || callContractResult.getTransactionReceipt().getResult() != 0) {
            System.out.println("callContract Error :" + getErrorMsg((int) callContractResult.getTransactionReceipt().getResult()));
            return "failed";
        } else {
            byte[] output = callContractResult.getTransactionReceipt().getOutput();
            if (output == null) {
                exit("call DataQuery function", "output failed");
                return "failed";
            } else {
                // decode return values
                EVMOutput contractReturnValues = new EVMOutput(ByteUtils.toHexString(output));
                System.out.println(String.format("call callContractDataQueryCredit function,log is %s", contractReturnValues.getString()));
                return contractReturnValues.getString();
            }
        }
    }

    public static List<SensorData> dataToSensorDataList1(Sensor sensor) {
        String data = callContractDataQueryCredit1(sensor);
        String[] sensorData = data.split("/");
        List<SensorData> sensorList = new ArrayList<SensorData>();
        for (int i = 0; i < sensorData.length; i++) {
            String[] tmp = sensorData[i].split("-");
            SensorData sensorData1 = new SensorData();
            sensorData1.setId(sensor.getId());
            Long tmp1 = Long.parseLong(tmp[0]);
            Date date = new Date(tmp1);
            sensorData1.setCreteTime(date);
            sensorData1.setPh(Integer.parseInt(tmp[1]));
            sensorData1.setChroma(Integer.parseInt(tmp[2]));
            sensorData1.setSs(Integer.parseInt(tmp[3]));
            sensorData1.setBod5(Integer.parseInt(tmp[4]));
            sensorData1.setCod(Integer.parseInt(tmp[5]));
            sensorData1.setAn(Integer.parseInt(tmp[6]));
            sensorData1.setTn(Integer.parseInt(tmp[7]));
            sensorData1.setTp(Integer.parseInt(tmp[8]));
            sensorData1.setVp(Integer.parseInt(tmp[9]));
            sensorData1.setToc(Integer.parseInt(tmp[10]));
            sensorData1.setStatus(Integer.parseInt(tmp[11]));
            sensorList.add(sensorData1);

        }
        return sensorList;
    }

    public static List<SensorData> dataToSensorDataList2(Sensor sensor, Date startTime, Date endTime) {
        String data = callContractDataQueryCredit2(sensor, startTime, endTime);
        String[] sensorData = data.split("/");
        List<SensorData> sensorList = new ArrayList<SensorData>();
        for (int i = 0; i < sensorData.length; i++) {
            String[] tmp = sensorData[i].split("-");
            SensorData sensorData1 = new SensorData();
            sensorData1.setId(sensor.getId());
            Long tmp1 = Long.parseLong(tmp[0]);
            Date date = new Date(tmp1);
            sensorData1.setCreteTime(date);
            sensorData1.setPh(Integer.parseInt(tmp[1]));
            sensorData1.setChroma(Integer.parseInt(tmp[2]));
            sensorData1.setSs(Integer.parseInt(tmp[3]));
            sensorData1.setBod5(Integer.parseInt(tmp[4]));
            sensorData1.setCod(Integer.parseInt(tmp[5]));
            sensorData1.setAn(Integer.parseInt(tmp[6]));
            sensorData1.setTn(Integer.parseInt(tmp[7]));
            sensorData1.setTp(Integer.parseInt(tmp[8]));
            sensorData1.setVp(Integer.parseInt(tmp[9]));
            sensorData1.setToc(Integer.parseInt(tmp[10]));
            sensorData1.setStatus(Integer.parseInt(tmp[11]));
            sensorList.add(sensorData1);
        }
        return sensorList;
    }

    // function AddLogData(uint _number, string _addr, uint _score, string _operator, string _description) returns (bool)
    public static boolean callContractAddLogDataCredit(Sensor sensor, Score score, String operation, ScDescription scDescription) {
        EVMParameter parameters = new EVMParameter("AddLogData(uint256,string,uint256,string,string)");
        parameters.addUint(BigInteger.valueOf(sensor.getId()));
        parameters.addString((sensor.getAddress()));
        parameters.addUint(BigInteger.valueOf(score.getNum()));
        parameters.addString(operation);
        parameters.addString(scDescription.getDescription());

        // build CallContractRequest
        CallContractRequest request = new CallContractRequest(userIdentity,
                Utils.getIdentityByName(callContractId), parameters, BigInteger.ZERO, VMTypeEnum.EVM);

        TransactionReceiptResponse callContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);

            callContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            callContractResult = sdk.getContractService().callContract(request);
        }

        if (!callContractResult.isSuccess() || callContractResult.getTransactionReceipt().getResult() != 0) {
            System.out.println("callContract Error :" + getErrorMsg((int) callContractResult.getTransactionReceipt().getResult()));
            return false;
        } else {
            byte[] output = callContractResult.getTransactionReceipt().getOutput();
            if (output == null) {
                exit("call AddLogData function", "output failed");
                return false;
            } else {
                // decode return values
                EVMOutput contractReturnValues = new EVMOutput(ByteUtils.toHexString(output));
                System.out.println(String.format("call callContractAddLogDataCredit function, response value: ", contractReturnValues.getBoolean()));
                return true;
            }
        }
    }

    // function QueryLogData(uint _number, string _addr) returns (string)
    public static String callContractQueryLogDataCredit1(Sensor sensor) {
        EVMParameter parameters = new EVMParameter("QueryLogData(uint256,string)");
        parameters.addUint(BigInteger.valueOf(sensor.getId()));
        parameters.addString(sensor.getAddress());

        // build CallContractRequest
        CallContractRequest request = new CallContractRequest(userIdentity,
                Utils.getIdentityByName(callContractId), parameters, BigInteger.ZERO, VMTypeEnum.EVM);

        TransactionReceiptResponse callContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);

            callContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            callContractResult = sdk.getContractService().callContract(request);
        }

        if (!callContractResult.isSuccess() || callContractResult.getTransactionReceipt().getResult() != 0) {
            System.out.println("callContract Error :" + getErrorMsg((int) callContractResult.getTransactionReceipt().getResult()));
            return "failed1";
        } else {
            byte[] output = callContractResult.getTransactionReceipt().getOutput();
            if (output == null) {
                exit("call QueryLogData function", "output failed");
                return "failed2";
            } else {
                // decode return values
                EVMOutput contractReturnValues = new EVMOutput(ByteUtils.toHexString(output));
                System.out.println(String.format("call callContractQueryLogDataCredit function,result is %s", contractReturnValues.getString()));
                return contractReturnValues.getString();
            }
        }
    }

    public static String callContractQueryLogDataCredit2(Sensor sensor, Date startTime, Date endTime) {
        EVMParameter parameters = new EVMParameter("QueryLogData(uint256,string,uint256,uint256)");
        parameters.addUint(BigInteger.valueOf(sensor.getId()));
        parameters.addString(sensor.getAddress());
        parameters.addUint(BigInteger.valueOf(startTime.getTime()));
        parameters.addUint(BigInteger.valueOf(endTime.getTime()));

        // build CallContractRequest
        CallContractRequest request = new CallContractRequest(userIdentity,
                Utils.getIdentityByName(callContractId), parameters, BigInteger.ZERO, VMTypeEnum.EVM);

        TransactionReceiptResponse callContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);

            callContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            callContractResult = sdk.getContractService().callContract(request);
        }

        if (!callContractResult.isSuccess() || callContractResult.getTransactionReceipt().getResult() != 0) {
            System.out.println("callContract Error :" + getErrorMsg((int) callContractResult.getTransactionReceipt().getResult()));
            return "failed1";
        } else {
            byte[] output = callContractResult.getTransactionReceipt().getOutput();
            if (output == null) {
                exit("call QueryLogData function", "output failed");
                return "failed2";
            } else {
                // decode return values
                EVMOutput contractReturnValues = new EVMOutput(ByteUtils.toHexString(output));
                System.out.println(String.format("call callContractQueryLogDataCredit function,result is %s", contractReturnValues.getString()));
                return contractReturnValues.getString();
            }
        }
    }

    //callContractQueryLogDataCredit1
    public static List<LogDataParam> logDataToMap1(Sensor sensor) {
        String logData = callContractQueryLogDataCredit1(sensor);
        String[] logDataArray = logData.split("/");
        List<LogDataParam> list = new ArrayList<LogDataParam>();
        for (int i = 0; i < logDataArray.length; i++) {
            LogDataParam logDataParam = new LogDataParam();
            String[] tmp = logDataArray[i].split("-");
            logDataParam.setSensorId(sensor.getId());
            logDataParam.setAddress(tmp[1]);
            logDataParam.setDescription(tmp[4] + " " + tmp[3] + tmp[2]);
            logDataParam.setCreateTime(new Date(Long.parseLong(tmp[5])));
            list.add(logDataParam);
        }
//        return mapList;
        return list;
    }

    //callContractQueryLogDataCredit2
    public static List<LogDataParam> logDataToMap2(Sensor sensor, Date startTime, Date endTime) {
        String logData = callContractQueryLogDataCredit2(sensor, startTime, endTime);
        String[] logDataArray = logData.split("/");
        List<LogDataParam> list = new ArrayList<LogDataParam>();
        for (int i = 0; i < logDataArray.length; i++) {
            LogDataParam logDataParam = new LogDataParam();
            String[] tmp = logDataArray[i].split("-");
            logDataParam.setSensorId(sensor.getId());
            logDataParam.setAddress(tmp[1]);
            logDataParam.setDescription(tmp[4] + " " + tmp[3] + tmp[2]);
            logDataParam.setCreateTime(new Date(Long.parseLong(tmp[5])));
            list.add(logDataParam);
        }
        return list;
    }

    //升级合约
    public static void updateContractDemo() {
        EVMParameter contractParameters = new EVMParameter();
        UpdateContractRequest request = new UpdateContractRequest(Utils.getIdentityByName(callContractId), contractUpdateCode, VMTypeEnum.EVM);
        UpdateContractResponse updateContractResponse = sdk.getContractService().updateContract(request);

        // deploy contract
        if (!updateContractResponse.isSuccess()
                || updateContractResponse.getTransactionReceipt().getResult() != 0) {
            exit("upgrade Contract",
                    getErrorMsg((int) updateContractResponse.getTransactionReceipt().getResult()));
        } else {
            System.out.println("upgrade contract success.contact id is " + callContractId);
        }
    }

    //调用升级新方法
    public static void callContractGetParamsTest() {
        EVMParameter parameters = new EVMParameter("GetParamsTest()");

        // build CallContractRequest
        CallContractRequest request = new CallContractRequest(userIdentity,
                Utils.getIdentityByName(callContractId), parameters, BigInteger.ZERO, VMTypeEnum.EVM);

        TransactionReceiptResponse callContractResult;
        if (isTeeChain) {
            signRequest(request);
            // generate transaction key
            byte[] transactionKey = ConfidentialUtil.keyGenerate(secretKey,
                    request.getTransaction().getHash().getValue());

            ConfidentialRequest confidentialRequest = new ConfidentialRequest(request, publicKeys, transactionKey);

            callContractResult = sdk.getConfidentialService().confidentialRequest(confidentialRequest);
        } else {
            callContractResult = sdk.getContractService().callContract(request);
        }

        if (!callContractResult.isSuccess() || callContractResult.getTransactionReceipt().getResult() != 0) {
            System.out.println("callContract Error :" + getErrorMsg((int) callContractResult.getTransactionReceipt().getResult()));
        } else {
            byte[] output = callContractResult.getTransactionReceipt().getOutput();
            if (output == null) {
                exit("call callContractGetParamsTest function", "output failed");
            } else {
                // decode return values
                EVMOutput contractReturnValues = new EVMOutput(ByteUtils.toHexString(output));

                BigInteger bigInteger = contractReturnValues.getUint(); // 100
                String string1 = contractReturnValues.getString();       // "abc"
                String string2 = contractReturnValues.getString();
                boolean isOK = contractReturnValues.getBoolean();
                String id = contractReturnValues.getIdentity().toString();
                System.out.println("call callContractGetParamsTest function OK");
            }
        }
    }


    //冻结合约
    public static void freezeContractTest() {
        FreezeContractRequest request = new FreezeContractRequest(userIdentity, Utils.getIdentityByName(callContractId));
        FreezeContractResponse freezeContractResponse = sdk.getContractService().freezeContract(request);
        if (!freezeContractResponse.isSuccess()
                || freezeContractResponse.getTransactionReceipt().getResult() != 0) {
            exit("freeze Contract",
                    getErrorMsg((int) freezeContractResponse.getTransactionReceipt().getResult()));
        } else {
            System.out.println("freeze contract success.contact id is " + callContractId);
        }
    }

    //解冻合约
    public static void unFreezeContractTest() {
        UnFreezeContractRequest request = new UnFreezeContractRequest(userIdentity, Utils.getIdentityByName(callContractId));
        UnFreezeContractResponse unFreezeContractResponse = sdk.getContractService().unFreezeContract(request);
        if (!unFreezeContractResponse.isSuccess()
                || unFreezeContractResponse.getTransactionReceipt().getResult() != 0) {
            exit("freeze Contract",
                    getErrorMsg((int) unFreezeContractResponse.getTransactionReceipt().getResult()));
        } else {
            System.out.println("unFreeze contract success.contact id is " + callContractId);
        }
    }

    //订阅合约
    public static void listenContractTest() {
        //event handler
        IEventCallback handler = new IEventCallback() {
            @Override
            public void onEvent(Message message) {
                PushContractEvent eventContractMessage = (PushContractEvent) message;
                // code
            }
        };

        BigInteger eventId = sdk.getEventService().listenContract(userIdentity, handler, EventModelType.PUSH);
        if (eventId.longValue() == 0) {
            System.out.println("listenContract failed");
        }
    }

    public static void main(String[] args) throws Exception {
        //step 1:init mychain env.
        initMychainEnv();
        //step 2: init sdk client
        initSdk();

//        System.out.println(new Date(new Date().getTime()));
        //step 3 : deploy a contract using useridentity.
//        deployContract();
//        System.out.println(new Date(new Date().getTime()));
        //调用合约的过程
        //1 添加一个传感器
//        boolean result = callContractAddSensorCredit(1, "武汉");
//        System.out.println(result);
        //2 给传感器添加一个数据
        /*Sensor sensor = new Sensor();
        sensor.setId(1L);
        sensor.setAddress("武汉");
        SensorData sensorData = new SensorData();
        sensorData.setId(1L);
        sensorData.setCreteTime(new Date());
        sensorData.setPh(1);
        sensorData.setChroma(1);
        sensorData.setSs(1);
        sensorData.setBod5(1);
        sensorData.setCod(1);
        sensorData.setAn(1);
        sensorData.setTn(1);
        sensorData.setTp(1);
        sensorData.setVp(1);
        sensorData.setToc(1);
        sensorData.setStatus(0);*/
//        boolean result = callContractDataReceiveCredit(sensor, sensorData);
//        System.out.println(result);
        //3 根据sensor查到数据（栈溢出）
//        Sensor sensor = new Sensor();
//        sensor.setId(1L);
//        sensor.setAddress("武汉");
//        String result = callContractDataQueryCredit(sensor);
//        System.out.println(result);
        //step 4 callContract.
        //upgrade contract 调用
        //updateContractDemo();
        //callContractGetParamsTest();


        //freezeContract调用
        //freezeContractTest();
        //callContractQueryCredit(account);

        //unFreeze调用
        //unFreezeContractTest();

//        System.in.read();
        //step 5 : sdk shut down
//        sdk.shutDown();

    }
}
