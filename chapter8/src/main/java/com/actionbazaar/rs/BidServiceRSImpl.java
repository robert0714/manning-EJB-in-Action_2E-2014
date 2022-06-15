/**
 *  BidServiceRSImpl.java
 *  EJB 3 in Action
 *  Book: http://manning.com/panda2/
 *  Code: http://code.google.com/p/action-bazaar/
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.actionbazaar.rs;

import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;
 
/**
 * BidServiceRSImpl
 */
public class BidServiceRSImpl implements BidServiceRS {
	private static final Logger logger = getLogger(BidServiceRSImpl.class.getName());
    @Override
    public void addBid(BidDTO bid) {
    	logger.info("addBid.............");
    	System.out.println("addBid.............");
    }

    @Override
	public BidDTO getBid(long bidId) {
		logger.info("getBid: " + bidId );
		System.out.println("getBid: " + bidId );
		BidDTO result =new BidDTO();
		result.setBidId(bidId);
		return result;
	}

    @Override
    public void cancelBid(long bidId) {
        // cancels a bid
    	logger.info("cancels a bid: " + bidId );
    	System.out.println("cancels a bid: " + bidId );
    }

    @Override
    public String listBids(String category, long userId, String startDate, String endDate) {
		logger.info(String.format("category: %s from %s to %s", category, startDate, endDate));
		System.out.println(String.format("category: %s from %s to %s", category, startDate, endDate));
		return "";
    }
    
}
