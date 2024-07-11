/*
 * Online auctioning system
 *
 * Copyright (c) 2023.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.controllers;
import com.skycastle.auction.clients.AuthClientStore;
import com.skycastle.auction.clients.ProductClientStore;
import com.skycastle.auction.dtos.*;
import com.skycastle.auction.dtos.bids.BidDTO;
import com.skycastle.auction.dtos.bids.BidRequestDTO;
import com.skycastle.auction.dtos.kafka.MessageDTO;
import com.skycastle.auction.entities.Auction;
import com.skycastle.auction.dtos.responses.AuctionResponseDTO;
import com.skycastle.auction.dtos.responses.BaseResponseDTO;
import com.skycastle.auction.dtos.responses.ListResponseDTO;
import com.skycastle.auction.entities.Bid;
import com.skycastle.auction.mappers.BidderMapper;
import com.skycastle.auction.repositories.AuctionRepository;
import com.skycastle.auction.services.AuctionService;
import com.skycastle.auction.services.BidService;
import com.skycastle.auction.services.TransactionalExecutorService;
import com.skycastle.auction.utils.ContextProvider;
import com.skycastle.auction.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
@Transactional
public class AuctionController {

    private final ContextProvider contextProvider;
    private final KafkaAdmin kafkaAdmin;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuctionService auctionService;
    private final AuctionRepository auctionRepository;
    private final AuthClientStore authClientStore;
    private final BidService bidService;
    private final BidderMapper bidderMapper;

    private final EntityManager entityManager;

    private final TransactionalExecutorService transactionalExecutorService;

    private final ProductClientStore productClientStore;


    protected User getBuyer(Long buyerId){
        String authHeader = contextProvider.getAuthHeaderValue();
        return authClientStore.getBuyer(authHeader, buyerId);
    }


    @PostMapping("/configure-auction-session")
    public ResponseEntity<BaseResponseDTO> createAuction(
            @RequestBody @Valid AuctionRequestDTO request, BindingResult bindingResult
    ){
        BaseResponseDTO response;
        try{
            response =  new BaseResponseDTO(bindingResult);
            if (!response.isSuccess()){
                return ResponseEntity.badRequest().body(response);
            }
            Auction auction;
            if(request.getId() != null){
                auction = auctionService.findById(request.getId());
            }else{
                auction = new Auction();
            }
            request.setMinBidAmount(request.getMinBidAmount());
            auction.setName(request.getName());
            auction.setSeller(request.getSeller());
            auction.setSellerName(request.getSellerName());
            auction.setProductImage(request.getProductImage());
            auction.setProduct(request.getProduct());
            auction.setSellingPrice(request.getSellingPrice());
            auction.setMinBidAmount(request.getMinBidAmount());
            auction.setStartDate(request.getStartDate());
            auction.setEndDate(request.getEndDate());
            auction.setReservePrice(request.getReservePrice());
            Auction newAuction = auctionService.setAuctionSession(auction);
            response.setMessage("Auction session has been configured for this product.");
            response.setData(newAuction);
            log.info("New auction %s has been configured for product %d".formatted(newAuction, request.getProduct()));
            return  ResponseEntity.ok(response);
        }catch (Exception e){
            log.error(e.getMessage());
            response =  new BaseResponseDTO(e);
            return  ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/auction/{auctionId}")
    public ResponseEntity<?> updatePartially(
            @PathVariable(name = "auctionId") Long auctionId,
            @RequestBody AuctionPatchDTO request
    ){
        try{
            Auction auction =  auctionService.findById(auctionId);
            if(auction != null){
                if (request.getMaxDuration() != null){
                    if(request.getStartDate() ==null){
                        return  ResponseEntity.badRequest().body("Bad request To update maxDuration, you must supply a startDate");
                    }
                    auction.setMaxDuration(request.getMaxDuration());
                }
                if(request.getStartDate() !=null){
                    auction.setStartDate(request.getStartDate());
                    // when  we update the startDate we can now tell what the endDate is;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(request.getStartDate());
                    calendar.add(Calendar.MINUTE, auction.getMaxDuration());
                    auction.setEndDate(calendar.getTime());
                }
                if(request.getStatus() != null){
                    auction.setStatus(Utils.AUCTION_STATUS.valueOf(request.getStatus()));
                }
                if(request.getReservePrice()  != null){
                    auction.setReservePrice(request.getReservePrice());
                }
                if(request.getMinBidAmount()  != null){
                    auction.setMinBidAmount(request.getMinBidAmount());
                }
                if (request.getSellingPrice() != null){
                    auction.setSellingPrice(request.getSellingPrice());
                }
                return ResponseEntity.ok(auctionRepository.save(auction));
            }
            return ResponseEntity.badRequest().body("Bad request");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/auction/{auctionId}")
    public  ResponseEntity<BaseResponseDTO> findAuctionById(@PathVariable Long auctionId){
        BaseResponseDTO  response;
        try{
            response =  new BaseResponseDTO();
            Auction auction = auctionService.findById(auctionId);
            Object vehicle = productClientStore.getProduct(contextProvider.getAuthHeaderValue(), auction.getProduct());
            AuctionResponseDTO auctionResponseDTO = bidderMapper.toAuctionResponseDTO(auction);
            auctionResponseDTO.setVehicle(vehicle);
            response.setData(auctionResponseDTO);
            return ResponseEntity.ok(response);
        }catch (Exception e){
           response = new BaseResponseDTO(e);
           return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/auction/product")
    public ResponseEntity<?> findAuctionByProduct(
            @RequestParam(name = "productId") Long productId
    ){
        BaseResponseDTO response = new BaseResponseDTO();
        try{
            Auction auction = auctionService.findAuctionByProductId(productId);
            if(auction != null){
                log.info("Fetched auction session %s".formatted(auction));
                Object vehicle = productClientStore.getProduct(contextProvider.getAuthHeaderValue(), auction.getProduct());
                AuctionResponseDTO auctionResponseDTO = bidderMapper.toAuctionResponseDTO(auction);
                auctionResponseDTO.setVehicle(vehicle);
                response.setData(auctionResponseDTO);
                return ResponseEntity.ok(response);
            }
            response.setData(null);
            response.setMessage("No auctions configured");
            return  ResponseEntity.ok(response);

        }catch (Exception e){
            log.error(e.getMessage(),e);
            return  ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/auctions")
    public ResponseEntity<?> getAuctions(
            @RequestParam(value = "lower_date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date lowerDate,
            @RequestParam(value = "upper_date",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date upperDate,
            @RequestParam(value = "status", required = false) Utils.AUCTION_STATUS status,
            @RequestParam(value = "seller", required = false) Long seller,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        List<AuctionResponseDTO> auctions;
        ListResponseDTO<AuctionResponseDTO>  response;
        Page<Auction> p;
        User u = contextProvider.getPrincipal();
        if (u.getRole().equals(Utils.ROLES.ROLE_SELLER)){
            seller =  u.getId();
        }
        if (seller != null){
            status = status == null ? Utils.AUCTION_STATUS.PENDING : status;
            p = auctionService.findAuctionsBySellerAndStatus(seller, status, page, size);
        }
        else if(status !=null && upperDate != null){
            p = auctionService.filterAuctionsStatusAndByDateRange(status, lowerDate, upperDate, page, size);
        } else if (status != null) {
            p =  auctionService.filterByStatus(status, page, size);
        } else if (upperDate != null){
            p = auctionService.filterAuctionsByDateRange(lowerDate, upperDate, page, size);
        }
        else{
            p = auctionService.fetchAll(page, size);
        }
        auctions = p.getContent().stream().map(bidderMapper::toAuctionResponseDTO).collect(Collectors.toList());
        response = new ListResponseDTO<>(auctions);
        response.setTotalCount(p.getTotalElements());
        response.setPage(p.getNumber());
        response.setTotalPages(p.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping("submit-bid")
    public ResponseEntity<?> recordBid( @RequestBody BidRequestDTO bidRequest){
        MessageDTO message;
        try{
            User buyer =  getBuyer(bidRequest.getBuyer());
            Auction auction = auctionService.findAuctionByProductId(bidRequest.getProductId());

            if (bidRequest.getAmount().doubleValue() < auction.getMinBidAmount().doubleValue()){
                return ResponseEntity.badRequest().body(String.format("Bid amount of UGX: %s is less than the minimum bid amount of UGX: %s", bidRequest.getAmount(), auction.getMinBidAmount()));
            }
            BidDTO newBid = new BidDTO(bidService.saveBid(bidRequest, auction));
            entityManager.refresh(auction);
            newBid.setAuction(auction);

            Map<String, Object> buyerObj = new HashMap<>();
            buyerObj.put("id", buyer.getId());
            buyerObj.put("username", buyer.getUsername());
            newBid.setBuyer(buyerObj);
            message =  new MessageDTO(newBid);
            AuctionResponseDTO auctionResponseDTO = bidderMapper.toAuctionResponseDTO(auction);
            kafkaTemplate.send("auction-session", message); // publish to kafka topic
            return ResponseEntity.ok(auctionResponseDTO);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("buy-now")
    public ResponseEntity<?> buyNow(
            @RequestParam(name="vehicle") Long vehicleId,
            @RequestParam(name="buyer") Long buyerId,
            @RequestParam(name="amount") Double buyNowAmount
    ){
      try{
          User buyer =  getBuyer(buyerId);
          if(buyer == null){
              return ResponseEntity.badRequest().body("Buyer id "+ buyerId + " does not exist" );
          }
          // TODO confirm vehicle exists
          Auction auction =  auctionService.findAuctionByProductId(vehicleId);
          if (auction == null){
              return ResponseEntity.badRequest().body("No auction configured for this vehicle");
          }
          Double sellPrice =  auction.getSellingPrice().doubleValue();
          if(sellPrice.equals(0.0)){
              return ResponseEntity.badRequest().body("No auction configured for this vehicle");
          }
          if (buyNowAmount.compareTo(sellPrice) < 0){
              return ResponseEntity.badRequest().body(String.format("Submitted buying price %s is less than the set selling price %s for this vehicle", buyNowAmount,sellPrice));
          }
          transactionalExecutorService.execInTransaction(()->{
              // Register a bid
              BidRequestDTO bidRequest =  new BidRequestDTO();
              bidRequest.setAmount(BigDecimal.valueOf(buyNowAmount));
              bidRequest.setBuyer(buyerId);
              bidRequest.setProductId(vehicleId);
              bidService.saveBid(bidRequest, auction);
              auctionService.completeAuction(auction);
              // Update the vehicle details in inventory to mark it purchased
              productClientStore.markSold(contextProvider.getAuthHeaderValue(), vehicleId, buyerId);
          });
          AuctionResponseDTO response = new AuctionResponseDTO(auction);
            return ResponseEntity.ok(response);
      }catch (Exception e){
          log.error(e.getMessage(),e);
          return ResponseEntity.internalServerError().body(e.getMessage());
      }
    }

}
