/*
 * Online auctioning system
 * Copyright (C) 2023 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>
 *
 * This program is not free software
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 */

--- functionTO ADD a record to accounts whenever a new client has been created
CREATE OR REPLACE FUNCTION update_auction_total_bid_amount()
returns trigger as '
BEGIN

UPDATE auction SET total_amount=total_amount+NEW.amount  WHERE id=NEW.auction_id;

return NEW;
END;
' LANGUAGE plpgsql;
