package com.worldcretornica.plotme;

import org.bukkit.block.data.BlockData;
import org.bukkit.Material;

public class MatAndData {
    public Material mat;
    public BlockData data;
    
    public MatAndData(final Material material, final BlockData data) {
        this.mat = material;
        this.data = data;
    }
}
