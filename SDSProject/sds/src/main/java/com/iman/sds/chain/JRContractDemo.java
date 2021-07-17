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

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris
 * @date 2021/5/29 18:22
 * @Email:gang.wu@nexgaming.com
 */
public class JRContractDemo {
    private static String contractCodeString = "608060405234801561001057600080fd5b506115ea806100206000396000f30060806040526004361061006d576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063491cfe6e146100725780634e958d65146100fd5780636275d1251461018f5780639165f6381461027b578063a76d54ff1461036a575b600080fd5b34801561007e57600080fd5b506100e360048036038101908080359060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610410565b604051808215151515815260200191505060405180910390f35b34801561010957600080fd5b5061016e60048036038101908080359060200190929190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506105b6565b60405180831515151581526020018281526020019250505060405180910390f35b34801561019b57600080fd5b5061020060048036038101908080359060200190929190803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061071c565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610240578082015181840152602081019050610225565b50505050905090810190601f16801561026d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561028757600080fd5b5061035060048036038101908080359060200190929190803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929080359060200190929190803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919080359060200190929190505050610d9b565b604051808215151515815260200191505060405180910390f35b34801561037657600080fd5b50610395600480360381019080803590602001909291905050506110b8565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156103d55780820151818401526020810190506103ba565b50505050905090810190601f1680156104025780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b600061041a61146a565b838160000181815250508281602001819052506000819080600181540180825580915050906001820390600052602060002090600c0201600090919290919091506000820151816000015560208201518160010190805190602001906104819291906114cc565b50604082015181600201908051906020019061049e92919061154c565b5060608201518160030190805190602001906104bb92919061154c565b5060808201518160040190805190602001906104d892919061154c565b5060a08201518160050190805190602001906104f592919061154c565b5060c082015181600601908051906020019061051292919061154c565b5060e082015181600701908051906020019061052f92919061154c565b5061010082015181600801908051906020019061054d92919061154c565b5061012082015181600901908051906020019061056b92919061154c565b5061014082015181600a01908051906020019061058992919061154c565b5061016082015181600b0190805190602001906105a792919061154c565b50505050600191505092915050565b60008060008090505b600080549050811015610709576000818154811015156105db57fe5b90600052602060002090600c020160000154851480156106eb575060008181548110151561060557fe5b90600052602060002090600c020160010160405180828054600181600116156101000203166002900480156106715780601f1061064f576101008083540402835291820191610671565b820191906000526020600020905b81548152906001019060200180831161065d575b5050915050604051809103902060001916846040518082805190602001908083835b6020831015156106b85780518252602082019150602081019050602083039250610693565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902060001916145b156106fc5760018192509250610714565b80806001019150506105bf565b600080809050925092505b509250929050565b606060008060606000606061073188886105b6565b809550819650505084151561074557600080fd5b6040805190810160405280600781526020017f6f75747075743a000000000000000000000000000000000000000000000000008152509250600091505b60008481548110151561079157fe5b90600052602060002090600c020160020180549050821015610d8d576107b9600183016110b8565b90506107fa836040805190810160405280600381526020017f6461790000000000000000000000000000000000000000000000000000000000815250611299565b92506108068382611299565b9250610847836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b92506108928361088d60008781548110151561085f57fe5b90600052602060002090600c02016002018581548110151561087d57fe5b90600052602060002001546110b8565b611299565b92506108d3836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b925061091e836109196000878154811015156108eb57fe5b90600052602060002090600c02016003018581548110151561090957fe5b90600052602060002001546110b8565b611299565b925061095f836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b92506109aa836109a560008781548110151561097757fe5b90600052602060002090600c02016004018581548110151561099557fe5b90600052602060002001546110b8565b611299565b92506109eb836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b9250610a3683610a31600087815481101515610a0357fe5b90600052602060002090600c020160050185815481101515610a2157fe5b90600052602060002001546110b8565b611299565b9250610a77836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b9250610ac283610abd600087815481101515610a8f57fe5b90600052602060002090600c020160060185815481101515610aad57fe5b90600052602060002001546110b8565b611299565b9250610b03836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b9250610b4e83610b49600087815481101515610b1b57fe5b90600052602060002090600c020160070185815481101515610b3957fe5b90600052602060002001546110b8565b611299565b9250610b8f836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b9250610bda83610bd5600087815481101515610ba757fe5b90600052602060002090600c020160080185815481101515610bc557fe5b90600052602060002001546110b8565b611299565b9250610c1b836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b9250610c6683610c61600087815481101515610c3357fe5b90600052602060002090600c020160090185815481101515610c5157fe5b90600052602060002001546110b8565b611299565b9250610ca7836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b9250610cf283610ced600087815481101515610cbf57fe5b90600052602060002090600c0201600a0185815481101515610cdd57fe5b90600052602060002001546110b8565b611299565b9250610d33836040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611299565b9250610d7e83610d79600087815481101515610d4b57fe5b90600052602060002090600c0201600b0185815481101515610d6957fe5b90600052602060002001546110b8565b611299565b92508180600101925050610782565b829550505050505092915050565b6000806000610daa8f8f6105b6565b8092508193505050811515610dbe57600080fd5b600081815481101515610dcd57fe5b90600052602060002090600c02016002018d9080600181540180825580915050906001820390600052602060002001600090919290919091505550600081815481101515610e1757fe5b90600052602060002090600c02016003018c9080600181540180825580915050906001820390600052602060002001600090919290919091505550600081815481101515610e6157fe5b90600052602060002090600c02016004018b9080600181540180825580915050906001820390600052602060002001600090919290919091505550600081815481101515610eab57fe5b90600052602060002090600c02016005018a9080600181540180825580915050906001820390600052602060002001600090919290919091505550600081815481101515610ef557fe5b90600052602060002090600c0201600601899080600181540180825580915050906001820390600052602060002001600090919290919091505550600081815481101515610f3f57fe5b90600052602060002090600c0201600701889080600181540180825580915050906001820390600052602060002001600090919290919091505550600081815481101515610f8957fe5b90600052602060002090600c0201600801879080600181540180825580915050906001820390600052602060002001600090919290919091505550600081815481101515610fd357fe5b90600052602060002090600c020160090186908060018154018082558091505090600182039060005260206000200160009091929091909150555060008181548110151561101d57fe5b90600052602060002090600c0201600a0185908060018154018082558091505090600182039060005260206000200160009091929091909150555060008181548110151561106757fe5b90600052602060002090600c0201600b018490806001815401808255809150509060018203906000526020600020016000909192909190915055506001925050509c9b505050505050505050505050565b6060600060606000806060600060649550856040519080825280601f01601f1916602001820160405280156110fc5781602001602082028038833980820191505090505b509450600093505b60008814151561119b57600a8881151561111a57fe5b069250600a8881151561112957fe5b049750826030017f010000000000000000000000000000000000000000000000000000000000000002858580600101965081518110151561116657fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a905350611104565b600184016040519080825280601f01601f1916602001820160405280156111d15781602001602082028038833980820191505090505b509150600090505b838111151561128857848185038151811015156111f257fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f010000000000000000000000000000000000000000000000000000000000000002828281518110151561124b57fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a90535080806001019150506111d9565b819650869650505050505050919050565b606080606080606060008088955087945084518651016040519080825280601f01601f1916602001820160405280156112e15781602001602082028038833980820191505090505b50935083925060009150600090505b85518110156113a357858181518110151561130757fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f010000000000000000000000000000000000000000000000000000000000000002838380600101945081518110151561136657fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a90535080806001019150506112f0565b600090505b845181101561145b5784818151811015156113bf57fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f010000000000000000000000000000000000000000000000000000000000000002838380600101945081518110151561141e57fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a90535080806001019150506113a8565b83965050505050505092915050565b610180604051908101604052806000815260200160608152602001606081526020016060815260200160608152602001606081526020016060815260200160608152602001606081526020016060815260200160608152602001606081525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061150d57805160ff191683800117855561153b565b8280016001018555821561153b579182015b8281111561153a57825182559160200191906001019061151f565b5b5090506115489190611599565b5090565b828054828255906000526020600020908101928215611588579160200282015b8281111561158757825182559160200191906001019061156c565b5b5090506115959190611599565b5090565b6115bb91905b808211156115b757600081600090555060010161159f565b5090565b905600a165627a7a723058200f692b0cbfb42a3254bc2b8225279840a971b2d240639f567e5cbdc014e00cb40029";
    private static byte[] contractCode = ByteUtils.hexStringToBytes(contractCodeString); //CreditManager

    //upgrade
    private static String contractUpdateCodeString = "60806040526004361061006d576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680631b3c4fab1461007257806357fce39d14610187578063af7c102c146101b2578063b2628df8146101f3578063d448601914610242575b600080fd5b34801561007e57600080fd5b50610087610291565b60405180868152602001806020018060200185151515158152602001848152602001838103835287818151815260200191508051906020019080838360005b838110156100e15780820151818401526020810190506100c6565b50505050905090810190601f16801561010e5780820380516001836020036101000a031916815260200191505b50838103825286818151815260200191508051906020019080838360005b8381101561014757808201518184015260208101905061012c565b50505050905090810190601f1680156101745780820380516001836020036101000a031916815260200191505b5097505050505050505060405180910390f35b34801561019357600080fd5b5061019c610337565b6040518082815260200191505060405180910390f35b3480156101be57600080fd5b506101dd60048036038101908080359060200190929190505050610341565b6040518082815260200191505060405180910390f35b3480156101ff57600080fd5b50610228600480360381019080803590602001909291908035906020019092919050505061035e565b604051808215151515815260200191505060405180910390f35b34801561024e57600080fd5b506102776004803603810190808035906020019092919080359060200190929190505050610523565b604051808215151515815260200191505060405180910390f35b6000606080600080606080600080600033905060c89250600091506040805190810160405280600781526020017f6a72626c6f636b0000000000000000000000000000000000000000000000000081525094506040805190810160405280601a81526020017f32303231303533316a72626c6f636b636f6e7261637463616c6c000000000000815250935082858584849950995099509950995050505050509091929394565b6000600254905090565b600060036000838152602001908152602001600020549050919050565b6000600254331415156103d9576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260118152602001807f5065726d697373696f6e2064656e69656400000000000000000000000000000081525060200191505060405180910390fd5b6000548260015401131580156103f457506001548260015401135b80156104005750600082135b1515610474576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600e8152602001807f496e76616c69642076616c75652100000000000000000000000000000000000081525060200191505060405180910390fd5b8160036000858152602001908152602001600020600082825401925050819055508160016000828254019250508190555081837f9a46fdc9277c739031110f773b36080a9a2012d0b3eca1f5ed8a3403973e05576001546040518080602001838152602001828103825260048152602001807f64656d6f000000000000000000000000000000000000000000000000000000008152506020019250505060405180910390a36001905092915050565b6000816003600033815260200190815260200160002054121515156105b0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260138152602001807f62616c616e6365206e6f7420656e6f756768210000000000000000000000000081525060200191505060405180910390fd5b6000821380156105c257506000548213155b1515610636576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600e8152602001807f496e76616c69642076616c75652100000000000000000000000000000000000081525060200191505060405180910390fd5b81600360003381526020019081526020016000206000828254039250508190555081600360008581526020019081526020016000206000828254019250508190555060019050929150505600a165627a7a72305820929f39f5dfc978f05e029b986659fd7542e1009cbbb133b2bc009f8876b59c910029";
    private static byte[] contractUpdateCode = ByteUtils.hexStringToBytes(contractUpdateCodeString); //CreditManager


    /**
     * contract id
     */
    private static String callContractId = "wuda1626447420742";

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


    private static void initMychainEnv() throws IOException {
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

    private static ClientEnv buildMychainEnv() throws IOException {
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

    private static void initSdk() {
        sdk = new MychainClient();
        boolean initResult = sdk.init(env);
        if (!initResult) {
            exit("initSdk", "sdk init failed.");
        } else {
            System.out.println("sdk init success");
        }
    }

    private static String getErrorMsg(int errorCode) {
        int minMychainSdkErrorCode = ErrorCode.SDK_INTERNAL_ERROR.getErrorCode();
        if (errorCode < minMychainSdkErrorCode) {
            return ErrorCode.valueOf(errorCode).getErrorDesc();
        } else {
            return ErrorCode.valueOf(errorCode).getErrorDesc();
        }
    }

    private static void exit(String tag, String msg) {
        exit(String.format("%s error : %s ", tag, msg));
    }

    private static void exit(String msg) {
        System.out.println(msg);
        System.exit(0);
    }

    private static void signRequest(AbstractTransactionRequest request) {
        // sign request
        long ts = sdk.getNetwork().getSystemTimestamp();
        request.setTxTimeNonce(ts, BaseFixedSizeUnsignedInteger.Fixed64BitUnsignedInteger
                .valueOf(RandomUtil.randomize(ts + request.getTransaction().hashCode())), true);
        request.complete();
        sdk.getConfidentialService().signRequest(env.getSignerOption().getSigners(), request);
    }

    private static void deployContract() {
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
    private static boolean callContractAddSensorCredit(Integer name, String addr) {
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

    private static boolean callContractDataReceiveCredit(Sensor sensor, SensorData sensorData) {
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

    private static String callContractDataQueryCredit(Sensor sensor) {
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
                System.out.println(String.format("call callContractDataQueryCredit function,log is %s", contractReturnValues.getString()));
                return contractReturnValues.getString();
            }
        }
    }

    // function AddLogData(uint _number, string _addr, uint _score, string _operator, string _description) returns (bool)
    private static boolean callContractAddLogDataCredit(Sensor sensor, Score score, String operation, ScDescription scDescription) {
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
    private static String callContractQueryLogDataCredit(Sensor sensor) {
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

    //升级合约
    private static void updateContractDemo() {
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
    private static void callContractGetParamsTest() {
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
    private static void freezeContractTest() {
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
    private static void unFreezeContractTest() {
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
    private static void listenContractTest() {
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

        //step 3 : deploy a contract using useridentity.
//        deployContract();
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
        Sensor sensor = new Sensor();
        sensor.setId(1L);
        sensor.setAddress("武汉");
        String result = callContractDataQueryCredit(sensor);
        System.out.println(result);
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
