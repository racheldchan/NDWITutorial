package tutorial

import geotrellis.raster._
import geotrellis.raster.io.geotiff._
import geotrellis.raster.render._
import com.typesafe.config.ConfigFactory

object CreateNDWIPng {
  val maskedPath = "data/r-nir.tif"
  val ndwiPath = "data/ndwi.png"

  def main(args: Array[String]): Unit = {
    val ndwi = {
      // Convert the tile to type double values,
      // because we will be performing an operation that
      // produces floating point values.
      println("Reading in multiband image...")
      val tile = MultibandGeoTiff(maskedPath).convert(DoubleConstantNoDataCellType)

      // Use the combineDouble method to map over the red and infrared values
      // and perform the NDWI calculation.
      println("Performing NDWI calculation...")
      tile.combineDouble(0, 1) { (g: Double, ir: Double) =>
        if(isData(g) && isData(ir)) {
          (g - ir) / (g + ir)
        } else {
          Double.NaN
        }
      }
    }

    // Get color map from the application.conf settings file.
    val colorMap = ColorMap.fromStringDouble(ConfigFactory.load().getString("tutorial.colormap")).get

    // Render this NDWI using the color breaks as a PNG,
    // and write the PNG to disk.
    println("Rendering PNG and saving to disk...")
    ndwi.renderPng(colorMap).write(ndwiPath)
  }
}