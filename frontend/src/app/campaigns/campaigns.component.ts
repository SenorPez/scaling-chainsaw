import {Component, OnInit} from '@angular/core';
import {ApiService} from '../api.service';
import {Campaign, EmbeddedCampaign, EmbeddedCharacter} from '../apiobjects';

@Component({
  selector: 'app-campaigns',
  templateUrl: './campaigns.component.html',
  styleUrls: ['./campaigns.component.css']
})
export class CampaignsComponent implements OnInit {
  campaigns: EmbeddedCampaign[];
  selectedCampaign: Campaign;
  selectedCampaignCharacters: EmbeddedCharacter[];

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.apiService.getIndex().subscribe(index => {
      this.apiService.getCampaigns(index).subscribe(campaigns => {
        this.campaigns = campaigns._embedded['loot-api:campaign'].sort((a, b) => {
          return a.name > b.name ? 1 : b.name > a.name ? -1 : 0;
        });
      });
    });
  }

  onClick(embeddedCampaign: EmbeddedCampaign) {
    this.apiService.getCampaign(embeddedCampaign).subscribe(campaign => {
      this.selectedCampaign = campaign;
      this.apiService.getCharacters(campaign).subscribe(characters => {
        this.selectedCampaignCharacters = characters._embedded['loot-api:character'].sort((a, b) => {
          return a.name > b.name ? 1 : b.name > a.name ? -1 : 0;
        });
      });
    });
  }
}
