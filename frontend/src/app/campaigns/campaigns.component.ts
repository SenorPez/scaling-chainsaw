import {Component, OnInit} from '@angular/core';
import {Campaign, Link} from '../apiobjects';
import {CampaignService} from '../campaign.service';

@Component({
  selector: 'app-campaigns',
  templateUrl: './campaigns.component.html',
  styleUrls: ['./campaigns.component.css']
})
export class CampaignsComponent implements OnInit {
  campaigns: Campaign[];
  indexLink: Link;

  selectedCampaign: Campaign;

  constructor(private campaignService: CampaignService) { }

  ngOnInit(): void {
    this.getCampaigns();
  }

  private getCampaigns(): void {
    this.campaignService.getCampaigns().subscribe(result => {
      this.campaigns = result._embedded['loot-api:campaign'];
      this.indexLink = result._links.index;
    });
  }

  onClick(campaign: Campaign) {
    this.selectedCampaign = campaign;
  }
}
