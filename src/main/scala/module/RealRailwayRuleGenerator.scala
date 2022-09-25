package metarules.module

import metarules.meta._
import Network._, Flags._, Flag._, RotFlip._, Implicits._
import scala.collection.mutable.Buffer
import NetworkProperties._


class RealRailwayRuleGenerator(val resolver: IdResolver) extends RuleGenerator {
  def start(): Unit = {
    /*
    Generate OxO rules by iteration over list of supported crossings
    */
    val RrwNetworks = List(L2Dtr)
    //val CrossNetworks = List(Road, L1Road, L2Road, Avenue, L1Avenue, L2Avenue)
    val CrossNetworks = List(Road, L1Road, L2Road, Avenue, L1Avenue, L2Avenue, Onewayroad, L1Onewayroad, L2Onewayroad,
    Rail, L1Dtr, L2Dtr)
    
    for (main <- RrwNetworks; base <- main.base) {

      Rules += main~WE | (base ~> main)~WE      // ortho
      Rules += main~WE | base~CW | % | main~WE  // overrides stub to orth ERRW

      for (minor <- CrossNetworks if minor.height != main.height) {

        Rules += main~WE | (base ~> main)~WE & minor~NS             // OxO
        Rules += main~WE | minor~NS | % | main~WE & minor~NS  // OxO no-int

        if(minor.typ == AvenueLike) {
          Rules += main~WE & minor~NS | (base ~> main)~WE & minor~SN // OxO double
          Rules += main~WE & minor~NS | minor~SN | % | main~WE & minor~SN // OxO double no-int
        }

        Rules += main~WE & minor~SN | (base ~> main)~WE       // OxO continue
        Rules += main~WE & minor~SN | base~CW | % | main~WE   // OxO continue stub conversion 
        Rules += main~WE & minor~SN | base~CE | % | main~WE   // OxO continue stub conversion
        
        for(minor2 <- CrossNetworks if minor2.height != main.height) {
          Rules += main~WE & minor~SN | (base ~> main)~WE & minor2~NS       // OxO | OxO adj
          Rules += main~WE & minor~SN | minor2~NS | % | main~WE & minor2~NS // OxO | OxO adj no-int
        }
        // Height transition OxO adjacency
        Rules += Rail~CW & main~CE | (base ~> main)~WE & minor~NS // Orth OST Adj
        Rules += Rail~CW & main~CE | minor~NS | % | main~WE & minor~NS // Orth OST no-int
        Rules += Rail~CW & main~WE | (base ~> main)~WE & minor~NS // Orth Ramp HT
        Rules += Rail~CW & main~WE | minor~NS | % | main~WE & minor~NS // Orth Ramp no-int
        
        /*

        Rules += main~WE | (base ~> main)~WE & minor~SE // OxD
        Rules += main~WE | minor~SE | % | main~WE & minor~SE // OxD no-int
        Rules += main~EW & minor~SW | (base ~> main)~WE // OxD continue
        for(minor2 <- CrossNetworks if minor2.height != main.height) {
          Rules += main~WE & minor~WN | (base ~> main)~WE & minor2~NS // OxD | OxO adjacencies
        }
        // Height transition OxD adjacency
        Rules += Rail~CW & main~CE | (base ~> main)~WE & minor~SE // Orth OST Adj
        Rules += Rail~CW & main~CE | minor~SE | % | main~WE & minor~SE// Orth OST no-int
        Rules += Rail~CW & main~WE | (base ~> main)~WE & minor~SE // Orth Ramp HT
        Rules += Rail~CW & main~WE | minor~SE | % | main~WE & minor~SE // Orth Ramp no-int

       //Rules += main~NE | (base ~> main)~NE & minor~WE // DxO
       */
      }
    }
    createRules()
  }
}