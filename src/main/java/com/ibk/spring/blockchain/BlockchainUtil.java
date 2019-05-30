package com.ibk.spring.blockchain;

import java.util.Properties;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.stereotype.Component;

/**
 * BlockchainUtil
 */
@Component
public class BlockchainUtil {

    // fabric peer에게 연결할 인스턴스
    HFClient hFClient;

    // ca에 연결할 인스턴스
    HFCAClient hFCAClient;

    // channel 인스턴스
    Channel channel;

    // ca 변수 설정
    final String CA_NAME = "ca.ibk.com";
    final String CA_URL = "http://192.168.56.10:7054";

    /**
     * @apiNote 1. 초기화 함수
     * @throws Exception
     */
    public void init() throws Exception {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        // hfclient 인스턴스 생성
        this.hFClient = HFClient.createNewInstance();

        // hfcaclient 인스턴스 생성
        this.hFCAClient = HFCAClient.createNewInstance(CA_NAME, CA_URL, new Properties());

        // crypto suite(암호화캡슐) 설정 - hfclient
        this.hFClient.setCryptoSuite(cryptoSuite);

        // crypto suite(암호화캡슐) 설정 - hfcaclient
        this.hFCAClient.setCryptoSuite(cryptoSuite);

    }

    /**
     * @apiNote 2. Create DefaultUser
     * @throws Exception
     */
    public void createDefaultUser() throws Exception {
        // CA한테 Enrollment 요청
        Enrollment enrollment = this.hFCAClient.enroll("admin", "adminpw");
        BlockchainUser blockchainUser = new BlockchainUser();
        blockchainUser.setName("admin");
        blockchainUser.setAffiliation("org");
        blockchainUser.setMspId("OrgMSP");
        blockchainUser.setEnrollment(enrollment);
        this.hFClient.setUserContext(blockchainUser);
    }

    /**
     * @apiNote 3. Connect Channel
     * @throws Exception
     */
    public void connectChannel() throws Exception{
        String peer_name="peer0.org.ibk.com";
        String peer_url="grpc://192.168.56.10:7051";

        
        String orderer_name="orderer.ibk.com";
        String orderer_url="grpc://192.168.56.10:7050";

        String channel_name="ibkchannel";

        //peer 설정 완료
        Peer peer = hFClient.newPeer(peer_name, peer_url);
        Orderer orderer = hFClient.newOrderer(orderer_name, orderer_url);

        this.channel = this.hFClient.newChannel(channel_name);

        this.channel.addPeer(peer);
        this.channel.addOrderer(orderer);

        this.channel.initialize();
    }

}