package com.skycastle.auction.controllers;
import com.skycastle.auction.clients.AuthClientStore;
import com.skycastle.auction.dtos.kafka.MessageDTO;
import com.skycastle.auction.entities.Auction;
import com.skycastle.auction.entities.Bid;
import com.skycastle.auction.dtos.User;
import com.skycastle.auction.dtos.bids.BidDTO;
import com.skycastle.auction.dtos.bids.BidRequestDTO;
import com.skycastle.auction.services.AuctionService;
import com.skycastle.auction.services.BidService;
import com.skycastle.auction.utils.ContextProvider;
import com.skycastle.auction.utils.Utils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LiveAuctionController {

    private final KafkaTemplate<String, Object>  kafkaTemplate;
    private final AuctionService auctionService;

    private final AuthClientStore authClientStore;
    private final BidService bidService;
    private final EntityManager entityManager;


    @MessageMapping("/bid/{uuid}") // uuid is auction uuid
    public void recordBidsInAuction(@DestinationVariable String uuid, @RequestBody BidRequestDTO bid, SimpMessageHeaderAccessor accessor){
        MessageDTO message;
        try{
            log.info("Recording bid "+ bid);
            List<String> headers =  accessor.getNativeHeader("Authorization");
            assert headers != null;
            String authHeader =   headers.get(0);
            Auction auction = auctionService.findAuctionByUuid(uuid);
            if (auction.getStatus() != Utils.AUCTION_STATUS.IN_PROGRESS){
                throw new Exception("This Auction is in state: "+ auction.getStatus()+ " and no bid submission is allowed");
            }
            User buyer =  authClientStore.getBuyer(authHeader, bid.getBuyer());
            BidDTO newBid = new BidDTO(bidService.saveBid(bid, auction));
            entityManager.refresh(auction);
            newBid.setAuction(auction);

            Map<String, Object> buyerObj = new HashMap<>();
            buyerObj.put("id", buyer.getId());
            buyerObj.put("username", buyer.getUsername());
            newBid.setBuyer(buyerObj);
            message =  new MessageDTO(newBid);

        }catch (Exception e){
            log.error(e.getMessage());
            message =  new MessageDTO(e, bid.getBuyer());
        }
        kafkaTemplate.send("auction-session", message);
    }

}
