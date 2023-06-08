package com.example.iec61850goosegenerator;

import lombok.Data;
import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.MacAddress;

import java.util.Optional;

import static org.reflections.Reflections.log;

@Data

public class SendingPackets {



    private String nicName;

    private PcapHandle handle;


    @SneakyThrows
    public void startInitialization() {
        if (handle == null) {
            initializeNetworkInteface();
        }
    }


    private Packet bildGoosePacket(GoosePacket packet) {
        byte[] payload = packet.createGoosePayload();

        UnknownPacket.Builder gooseBuilder = new UnknownPacket.Builder();
        gooseBuilder.rawData(payload);


        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder
                .dstAddr(MacAddress.getByName(packet.getMacDst()))
                .srcAddr(MacAddress.getByName(packet.getMacSrc()))
                .type(new EtherType((short) 0x88b8, "IEC61850/GOOSE" ))
                .payloadBuilder(gooseBuilder)
                .paddingAtBuild(true);
        return etherBuilder.build();
    }

    public void sendPackets(GoosePacket packet) {
        if (handle != null) {

            Packet p = bildGoosePacket(packet);

            try {
                handle.sendPacket(p);
                log.debug(p.toString());
            } catch (NotOpenException e) {
                throw new RuntimeException(e);
            } catch (PcapNativeException e) {
                throw new RuntimeException(e);
            }

        }
    }


    @SneakyThrows
    private void initializeNetworkInteface() {
        Optional<PcapNetworkInterface> nic = Pcaps.findAllDevs().stream()
                .filter(el -> nicName.equals(el.getDescription())).findFirst();

        if (nic.isPresent()) {
            handle = nic.get().openLive(1500, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
            log.info("network handler created {}", nic.get());
        } else {
            log.error("network interface not found" );
        }
    }
}
