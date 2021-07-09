package com.worldcretornica.plotme;

import java.util.List;
import java.util.HashMap;
import org.bukkit.block.data.BlockData;
import org.bukkit.Material;

public class PlotMapInfo {
    public int PlotSize;
    public int PlotAutoLimit;
    public int PathWidth;
    public Material BottomBlockId;
    public BlockData BottomBlockValue;
    public Material WallBlockId;
    public BlockData WallBlockValue;
    public Material PlotFloorBlockId;
    public BlockData PlotFloorBlockValue;
    public Material PlotFillingBlockId;
    public BlockData PlotFillingBlockValue;
    public Material RoadMainBlockId;
    public BlockData RoadMainBlockValue;
    public Material RoadStripeBlockId;
    public BlockData RoadStripeBlockValue;
    public HashMap<String, Plot> plots;
    public int RoadHeight;
    public int DaysToExpiration;
    public Material ProtectedWallBlockId;
    public Material ForSaleWallBlockId;
    public Material AuctionWallBlockId;
    public List<Material> ProtectedBlocks;
    public List<Material> PreventedItems;
    public boolean UseEconomy;
    public boolean CanPutOnSale;
    public boolean CanSellToBank;
    public boolean RefundClaimPriceOnReset;
    public boolean RefundClaimPriceOnSetOwner;
    public double ClaimPrice;
    public double ClearPrice;
    public double AddPlayerPrice;
    public double DenyPlayerPrice;
    public double RemovePlayerPrice;
    public double UndenyPlayerPrice;
    public double PlotHomePrice;
    public boolean CanCustomizeSellPrice;
    public double SellToPlayerPrice;
    public double SellToBankPrice;
    public double BuyFromBankPrice;
    public double AddCommentPrice;
    public double BiomeChangePrice;
    public double ProtectPrice;
    public double DisposePrice;
    public boolean AutoLinkPlots;
    public boolean DisableExplosion;
    public boolean DisableIgnition;
}
