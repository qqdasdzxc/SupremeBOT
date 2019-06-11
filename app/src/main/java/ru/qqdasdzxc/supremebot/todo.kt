package ru.qqdasdzxc.supremebot

//TODO

//возможно быстрее парсить самому штмльки

//подрубить фб для авторизации:
//экран логина для ввода кода

//бд фб:
//сам вручную вписываю временные коды для активации, после дропа их нужно удалить и создать новые
//либо написать скрипт в фб который сам удаляет

//код активации должен содержать счетчик активаций и уникальный идентификатор устройства
//когда пользователь вводит код и отправляет на сервер, берем у этого кода счетчик активаций
//изначально этот счетчик равен 0
//если счетчик = 0, то инкрементируем его и записываем идентификатор устройства в этот код
//если счетчик = 1 и идентификатор устройства совпадает с устройством пользователя, то пропускаем его на главный экран
//иначе пишем что код уже активирован, сорян


//ПОДУМАТЬ
//todo подумать что лучше - цепать сразу все элементы или пройтись по чайлдам вручную
//doc.child(0).child(1).child(2).child(1)
//doc.getElementsByAttributeValueContaining("class", "turbolink_scroller")
//doc.getElementsContainingOwnText("Apple Coaches")
//doc.allElements.firstOrNull { it.attributes().get("class") == "turbolink_scroller" }

//https://www.cleverbrush.com/editor/
//roboto

//список сайтов на будущее
//Footlocker
//Footaction
//Eastbay
//Champs
//Lacoste US/EU
//12amrun
//18montrose
//a-ma-maniere
//apbstore
//Addict Miami
//Anti Social Social Club
//Attic2zoo
//BBC Ice Cream US
//BEATNIC
//Black Market
//Blends
//Bodega
//Bows and Arrows
//Burn Rubber
//Concepts
//Courtside Sneakers
//DSM(EFLASH)
//Exclucity
//Extra Butter
//Feature
//History of NY
//Hotoveli
//Kith
//Kylie Cosmetics
//Lapston & Hammer
//Livestock
//MACHUS
//Marathon Sports
//minishopmadrid
//NRML
//NOIRFONCE
//Notre Shop
//OVO US
//Off The Hook
//Oi Polloi
//OMOCAT
//Oneness
//Packer Shoes
//Premier
//Proper LBC
//RSVP Gallery
//Renarts
//RIME NYC
//RISE
//Rock City Kicks
//Rooney
//Saint Alfred
//Sam Tabak
//Shoe Gallery Miami
//Shop Nice Kicks
//Sneaker Politics
//Sneaker World
//Social Status
//SoleFly
//Soleheaven
//Stampd
//Trophy Room
//Undefeated
//UNKNWN
//Vlone
//WishATL
//XHIBITION
//Yeezy Supply