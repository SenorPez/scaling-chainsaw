import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ApiService} from '../api.service';
import {Campaign, EmbeddedCharacter} from '../apiobjects';

@Component({
  selector: 'app-characters',
  templateUrl: './characters.component.html',
  styleUrls: ['./characters.component.css']
})
export class CharactersComponent implements OnInit, OnChanges {
  @Input() campaign: Campaign;
  characters: EmbeddedCharacter[];

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.getCharacters();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.campaign.currentValue !== changes.campaign.previousValue) {
      this.getCharacters();
    }
  }

  private getCharacters(): void {
    this.apiService.getCharacters(this.campaign).subscribe(characters => this.characters = characters._embedded['loot-api:character']);
  }
}
