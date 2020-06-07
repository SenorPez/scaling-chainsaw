import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';

import {AppComponent} from './app.component';
import {CampaignsComponent} from './campaigns/campaigns.component';
import {CharactersComponent} from './characters/characters.component';
import {CharacterComponent} from './character/character.component';

@NgModule({
  declarations: [
    AppComponent,
    CampaignsComponent,
    CharactersComponent,
    CharacterComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
