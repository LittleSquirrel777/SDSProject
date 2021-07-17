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
    private static String contractCodeString = "608060405234801561001057600080fd5b50611e32806100206000396000f300608060405260043610610078576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063491cfe6e1461007d5780634e958d65146101085780636275d1251461019a5780637a6a1880146102865780639165f638146103a7578063b4eca9d714610496575b600080fd5b34801561008957600080fd5b506100ee60048036038101908080359060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610582565b604051808215151515815260200191505060405180910390f35b34801561011457600080fd5b5061017960048036038101908080359060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610746565b60405180831515151581526020018281526020019250505060405180910390f35b3480156101a657600080fd5b5061020b60048036038101908080359060200190929190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506108ac565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561024b578082015181840152602081019050610230565b50505050905090810190601f1680156102785780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561029257600080fd5b5061038d60048036038101908080359060200190929190803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929080359060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610f59565b604051808215151515815260200191505060405180910390f35b3480156103b357600080fd5b5061047c60048036038101908080359060200190929190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192908035906020019092919080359060200190929190803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919080359060200190929190803590602001909291908035906020019092919050505061105f565b604051808215151515815260200191505060405180910390f35b3480156104a257600080fd5b5061050760048036038101908080359060200190929190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506114a4565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561054757808201518184015260208101905061052c565b50505050905090810190601f1680156105745780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b600061058c611c7b565b838160000181815250508281602001819052506000819080600181540180825580915050906001820390600052602060002090600d0201600090919290919091506000820151816000015560208201518160010190805190602001906105f3929190611ce4565b506040820151816002019080519060200190610610929190611d64565b50606082015181600301908051906020019061062d929190611d64565b50608082015181600401908051906020019061064a929190611d64565b5060a0820151816005019080519060200190610667929190611d64565b5060c0820151816006019080519060200190610684929190611d64565b5060e08201518160070190805190602001906106a1929190611d64565b506101008201518160080190805190602001906106bf929190611d64565b506101208201518160090190805190602001906106dd929190611d64565b5061014082015181600a0190805190602001906106fb929190611d64565b5061016082015181600b019080519060200190610719929190611d64565b5061018082015181600c019080519060200190610737929190611d64565b50505050600191505092915050565b60008060008090505b6000805490508110156108995760008181548110151561076b57fe5b90600052602060002090600d0201600001548514801561087b575060008181548110151561079557fe5b90600052602060002090600d020160010160405180828054600181600116156101000203166002900480156108015780601f106107df576101008083540402835291820191610801565b820191906000526020600020905b8154815290600101906020018083116107ed575b5050915050604051809103902060001916846040518082805190602001908083835b6020831015156108485780518252602082019150602081019050602083039250610823565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902060001916145b1561088c57600181925092506108a4565b808060010191505061074f565b600080809050925092505b509250929050565b6060600080606060006108bf8787610746565b80945081955050508315156108d357600080fd5b6040805190810160405280600781526020017f6f75747075743a000000000000000000000000000000000000000000000000008152509150600090505b60008381548110151561091f57fe5b90600052602060002090600d020160020180549050811015610f4c576109848261097f60008681548110151561095157fe5b90600052602060002090600d02016002018481548110151561096f57fe5b9060005260206000200154611950565b611aaa565b91506109c5826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610a1082610a0b6000868154811015156109dd57fe5b90600052602060002090600d0201600301848154811015156109fb57fe5b9060005260206000200154611950565b611aaa565b9150610a51826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610a9c82610a97600086815481101515610a6957fe5b90600052602060002090600d020160040184815481101515610a8757fe5b9060005260206000200154611950565b611aaa565b9150610add826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610b2882610b23600086815481101515610af557fe5b90600052602060002090600d020160050184815481101515610b1357fe5b9060005260206000200154611950565b611aaa565b9150610b69826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610bb482610baf600086815481101515610b8157fe5b90600052602060002090600d020160060184815481101515610b9f57fe5b9060005260206000200154611950565b611aaa565b9150610bf5826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610c4082610c3b600086815481101515610c0d57fe5b90600052602060002090600d020160070184815481101515610c2b57fe5b9060005260206000200154611950565b611aaa565b9150610c81826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610ccc82610cc7600086815481101515610c9957fe5b90600052602060002090600d020160080184815481101515610cb757fe5b9060005260206000200154611950565b611aaa565b9150610d0d826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610d5882610d53600086815481101515610d2557fe5b90600052602060002090600d020160090184815481101515610d4357fe5b9060005260206000200154611950565b611aaa565b9150610d99826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610de482610ddf600086815481101515610db157fe5b90600052602060002090600d0201600a0184815481101515610dcf57fe5b9060005260206000200154611950565b611aaa565b9150610e25826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610e7082610e6b600086815481101515610e3d57fe5b90600052602060002090600d0201600b0184815481101515610e5b57fe5b9060005260206000200154611950565b611aaa565b9150610eb1826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b9150610efc82610ef7600086815481101515610ec957fe5b90600052602060002090600d0201600c0184815481101515610ee757fe5b9060005260206000200154611950565b611aaa565b9150610f3d826040805190810160405280600381526020017f2f2f2f0000000000000000000000000000000000000000000000000000000000815250611aaa565b91508080600101915050610910565b8194505050505092915050565b6000806000610f66611db1565b610f708989610746565b8093508194505050821515610f8457600080fd5b888160000181815250508781602001819052508681604001818152505084816080018190525085816060018190525060018190806001815401808255809150509060018203906000526020600020906005020160009091929091909150600082015181600001556020820151816001019080519060200190611007929190611ce4565b5060408201518160020155606082015181600301908051906020019061102e929190611ce4565b50608082015181600401908051906020019061104b929190611ce4565b505050506001935050505095945050505050565b600080600061106e8f8f610746565b809250819350505081151561108257600080fd5b60008181548110151561109157fe5b90600052602060002090600d02016002018d90806001815401808255809150509060018203906000526020600020016000909192909190915055506000818154811015156110db57fe5b90600052602060002090600d02016003018c908060018154018082558091505090600182039060005260206000200160009091929091909150555060008181548110151561112557fe5b90600052602060002090600d02016004018b908060018154018082558091505090600182039060005260206000200160009091929091909150555060008181548110151561116f57fe5b90600052602060002090600d02016005018a90806001815401808255809150509060018203906000526020600020016000909192909190915055506000818154811015156111b957fe5b90600052602060002090600d020160060189908060018154018082558091505090600182039060005260206000200160009091929091909150555060008181548110151561120357fe5b90600052602060002090600d020160070188908060018154018082558091505090600182039060005260206000200160009091929091909150555060008181548110151561124d57fe5b90600052602060002090600d020160080187908060018154018082558091505090600182039060005260206000200160009091929091909150555060008181548110151561129757fe5b90600052602060002090600d02016009018690806001815401808255809150509060018203906000526020600020016000909192909190915055506000818154811015156112e157fe5b90600052602060002090600d0201600a0185908060018154018082558091505090600182039060005260206000200160009091929091909150555060008181548110151561132b57fe5b90600052602060002090600d0201600b0184908060018154018082558091505090600182039060005260206000200160009091929091909150555060068d10158015611378575060098d11155b80156113855750601e8c11155b80156113925750600a8b11155b801561139f5750600a8a11155b80156113ac575060328911155b80156113b9575060058811155b80156113c65750600f8711155b80156113d3575060028611155b80156113e0575060028511155b80156113ed575060148411155b156114425760008181548110151561140157fe5b90600052602060002090600d0201600c016000908060018154018082558091505090600182039060005260206000200160009091929091909150555061148e565b60008181548110151561145157fe5b90600052602060002090600d0201600c01600190806001815401808255809150509060018203906000526020600020016000909192909190915055505b6001925050509c9b505050505050505050505050565b6060600080606060006114b78787610746565b80945081955050508315156114cb57600080fd5b6040805190810160405280600781526020017f6f75747075743a000000000000000000000000000000000000000000000000008152509150600090505b6001805490508110156119435760018181548110151561152457fe5b90600052602060002090600502016000015487148015611634575060018181548110151561154e57fe5b906000526020600020906005020160010160405180828054600181600116156101000203166002900480156115ba5780601f106115985761010080835404028352918201916115ba565b820191906000526020600020905b8154815290600101906020018083116115a6575b5050915050604051809103902060001916866040518082805190602001908083835b60208310151561160157805182526020820191506020810190506020830392506115dc565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902060001916145b156119365761164b8261164689611950565b611aaa565b9150611667826020604051908101604052806000815250611aaa565b91506116738287611aaa565b91506116b4826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b91506116e8826116e36001848154811015156116cc57fe5b906000526020600020906005020160020154611950565b611aaa565b9150611729826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b91506117ed8260018381548110151561173e57fe5b90600052602060002090600502016003018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156117e35780601f106117b8576101008083540402835291602001916117e3565b820191906000526020600020905b8154815290600101906020018083116117c657829003601f168201915b5050505050611aaa565b915061182e826040805190810160405280600181526020017f2d00000000000000000000000000000000000000000000000000000000000000815250611aaa565b91506118f28260018381548110151561184357fe5b90600052602060002090600502016004018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156118e85780601f106118bd576101008083540402835291602001916118e8565b820191906000526020600020905b8154815290600101906020018083116118cb57829003601f168201915b5050505050611aaa565b9150611933826040805190810160405280600381526020017f2f2f2f0000000000000000000000000000000000000000000000000000000000815250611aaa565b91505b8080600101915050611508565b8194505050505092915050565b6060600080606060008086141561199e576040805190810160405280600181526020017f30000000000000000000000000000000000000000000000000000000000000008152509450611aa1565b8593505b6000841415156119c8578280600101935050600a848115156119c057fe5b0493506119a2565b826040519080825280601f01601f1916602001820160405280156119fb5781602001602082028038833980820191505090505b5091506001830390505b600086141515611a9a57600a86811515611a1b57fe5b066030017f010000000000000000000000000000000000000000000000000000000000000002828280600190039350815181101515611a5657fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a905350600a86811515611a9257fe5b049550611a05565b8194508494505b50505050919050565b606080606080606060008088955087945084518651016040519080825280601f01601f191660200182016040528015611af25781602001602082028038833980820191505090505b50935083925060009150600090505b8551811015611bb4578581815181101515611b1857fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000028383806001019450815181101515611b7757fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a9053508080600101915050611b01565b600090505b8451811015611c6c578481815181101515611bd057fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f0100000000000000000000000000000000000000000000000000000000000000028383806001019450815181101515611c2f57fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a9053508080600101915050611bb9565b83965050505050505092915050565b6101a060405190810160405280600081526020016060815260200160608152602001606081526020016060815260200160608152602001606081526020016060815260200160608152602001606081526020016060815260200160608152602001606081525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10611d2557805160ff1916838001178555611d53565b82800160010185558215611d53579182015b82811115611d52578251825591602001919060010190611d37565b5b509050611d609190611de1565b5090565b828054828255906000526020600020908101928215611da0579160200282015b82811115611d9f578251825591602001919060010190611d84565b5b509050611dad9190611de1565b5090565b60a06040519081016040528060008152602001606081526020016000815260200160608152602001606081525090565b611e0391905b80821115611dff576000816000905550600101611de7565b5090565b905600a165627a7a72305820fbc2e23e52ccc64826e4efd068b12b4a7909d63ba0860f82cd8f3491c316962d0029";
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

    public static String callContractDataQueryCredit(Sensor sensor) {
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
    public static String callContractQueryLogDataCredit(Sensor sensor) {
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

    public void initAntSDK() throws Exception{
        //step 1:init mychain env.
        initMychainEnv();
        //step 2: init sdk client
        initSdk();
    }

    public void shutDown(){
        sdk.shutDown();
    }
}
