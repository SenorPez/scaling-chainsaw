import {Component, OnInit} from '@angular/core';
import {Campaign} from '../apiobjects';
import {ApiService} from '../api.service';

@Component({
  selector: 'app-campaigns',
  templateUrl: './campaigns.component.html',
  styleUrls: ['./campaigns.component.css']
})
export class CampaignsComponent implements OnInit {
  campaigns: Campaign[];

  selectedCampaign: Campaign;

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.getCampaigns();
  }

  private getCampaigns(): void {
    this.apiService.getIndex().subscribe(index => {
      this.apiService.getCampaigns(index).subscribe(campaigns => this.campaigns = campaigns._embedded['loot-api:campaign']);
    });
  }

  onClick(campaign: Campaign) {
    this.selectedCampaign = campaign;
  }
}
